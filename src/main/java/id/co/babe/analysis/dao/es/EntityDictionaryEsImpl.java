package id.co.babe.analysis.dao.es;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkProcessor.Listener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class EntityDictionaryEsImpl implements EntityDictionaryEs {
	private static final Logger logger = LoggerFactory.getLogger(EntityDictionaryEsImpl.class);
	private String[] nodes;
	private String clusterName;
	private TransportClient client;
	private static final String indexName = "entity_dictionary";
	private static final String indexType = "dict";
	private static final String mapping = "{\"dict\": {\"properties\": {\"entity\": {\"type\":  \"string\", \"index\": \"not_analyzed\"}}}}";
	private static final String entity = "entity";
	/*private int bulkConcurency;
	private int bufferMB;
	private int actionSize;*/
	
	private BulkProcessor bulkProcessor;
	
	
	public EntityDictionaryEsImpl(String nodes, String clusterName) {
		this.nodes = nodes.split(",");
		this.clusterName = clusterName;
		
	}
	@Override
	public void init() throws NumberFormatException, UnknownHostException {
		
		//create tranportations
		Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName).build();
		client = TransportClient.builder().settings(settings).build();
		for (String node : nodes) {
			String[] hostPort = node.split(":");
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostPort[0]), Integer.valueOf(hostPort[1])));
		}
		bulkProcessor = BulkProcessor.builder(client, new Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				
				logger.error(String.format(
						"Found error after executing elastic bulk. bulkId : %s, size : %s, action size : %s",
						executionId, request.requests().size(), request.numberOfActions()), failure);
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				int size = request.requests().size();
				if (response.hasFailures()) {
					logger.error(
							"Found error after executing elastic bulk. bulkId : {}, size : {}, message : [{}], action size : {}",
							executionId, size, response.buildFailureMessage(), request.numberOfActions());
				} else {
					logger.info("Successfully executing bulk. bulkId : {}, size : {}, action size : {}", executionId, size,
							request.numberOfActions());
				}
			}
		}).build();
		
		//create entity_dictionary if it doesn't exist
		checkExistAndCreateIndex();
		
	}
	private void checkExistAndCreateIndex() {
		boolean isExisted =  client.admin().indices().exists(new IndicesExistsRequest(indexName)).actionGet().isExists();
		if (!isExisted){
            CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName);
            
            CreateIndexResponse response = createIndexRequestBuilder.addMapping(indexType, mapping).execute().actionGet();
            if(response.isAcknowledged()) {
                logger.info("Created ES index [{}] successfully", indexName);
                
            } else {
            	String errorMsg = String.format("Unable to create ES index [%s]", indexName);
                logger.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
        } else {
            logger.info("Index [{}] is already created. Ignore.", indexName);
        }
	}
	@Override
	public void insertWord(String word) {
		//String json = "{\"entity\":" + word + "}"; 
		Map<String, String> json = new HashMap<>();
		json.put(entity, word);
		IndexRequest request = client.prepareIndex(indexName, indexType).setSource(json).request();
		IndexResponse reponse = client.index(request).actionGet();
		logger.info("Successfully save [{}] items to ES index {}", word, indexName + "/" + indexType);
	}
	@Override
	public void insertWords(List<String> words) {
		for (String word : words) {
			Map<String, String> json = new HashMap<>();
			json.put(entity, word);
			//String json = "{\"entity\":" + word + "}"; 
			IndexRequest request = client.prepareIndex(indexName, indexType).setSource(json).request();
			bulkProcessor.add(request);
		}
		bulkProcessor.flush();
		//
		logger.info("Successfully save [{}] items to ES index {}", words.size(), indexName + "/" + indexType);
	}
	@Override
	public List<String> searchRegex(String regex) {
		QueryBuilder regexQueryBuilder = QueryBuilders.regexpQuery(entity, regex);
		
		return search(regexQueryBuilder);
	}
	@Override
	public List<String> searchPrefix(String prefix) {
		QueryBuilder filterBuilder = QueryBuilders.prefixQuery(entity, prefix);
		return search(filterBuilder);
	}
	private List<String> search(QueryBuilder queryBuilder) {
		List<String> ret = new ArrayList<>();
		//QueryBuilder filterBuilder = QueryBuilders.prefixQuery(entity, prefix);
		//BoolQueryBuilder r = QueryBuilders.boolQuery().filter(filterBuilder);
		
		SearchResponse response = client
				.prepareSearch(new String[] {indexName})
				.setTypes(new String[] {indexType})
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(queryBuilder)
				.setFrom(0).setSize(1000)
				.execute().actionGet();
		SearchHits hits = response.getHits();
		for(int i = 0; i < hits.totalHits(); i++) {
			SearchHit h = hits.getAt(i);
			ret.add((String)h.getSource().get("entity"));
		}
		return ret;
	}
	
	
	public static void main(String[] args) throws Exception {
		EntityDictionaryEs dict = new EntityDictionaryEsImpl("localhost:9300", "myES");
		
		dict.init();
		
		/*dict.insertWord("love");
		dict.insertWord("loves");
		dict.insertWord("loving");
		dict.insertWord("loved");*/
		
		/*List<String> words = new ArrayList<>();
		words.add("go");
		words.add("goes");
		words.add("going");
		dict.insertWords(words);*/
		
		List<String> rs = dict.searchPrefix("go");
		
		for (String s : rs) {
			System.out.println(s);
		}
		
	}
	
}