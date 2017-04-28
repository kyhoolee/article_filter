## Synopsis
The Name-Entity extraction library
- Extract the entity-candidate
- Filter the entity by dictionary
- Scoring the entity by appeared-position in document

## Code Example
Two main API from id.co.babe.analysis.CneAPI class

	/**
	 * 
	 * @param id_word
	 * @param stop_word
	 * @param tag_word
	 * @param redirect_word
	 * @param sent_parser
	 * @param token_parser
	 */
	public static void initDict(String id_word, String stop_word, String tag_word, String redirect_word, String sent_parser, String token_parser) {


	/**
	 * 
	 * @param text
	 * @return
	 */
	public Map<String, Double> extractEntity(String text) 

## Motivation

Filter the Name-Entity by using Crawled-dictionary: Wikipedia + Crawl-source
Combine exact-searching and similar-searching to detect Entity
Scoring the entity-document relation based on the position entity appears in document
Combine the Entity candidate to update the dictionary based on the frequency inside and outside document

Sample code and evaluation in id.co.babe.analsys.data.SolrClient

## License

A short snippet describing the license (MIT, Apache, etc.)
