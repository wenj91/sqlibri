package com.sqlibri.util;

public class PrettyStatus {

	public static String error(String query) {
		if(query.isEmpty()) return "Empty";
		StringBuilder result = new StringBuilder();

		result.append("SQL Error in query '");
		result.append(shortenQuery(query));
		result.append("'");
		
		return result.toString();
	}

	public static String success(String query, long milliseconds) {
		if(query.isEmpty()) return "Empty";
		StringBuilder result = new StringBuilder();

		result.append("Query '");
		result.append(shortenQuery(query));
		result.append("'");
		result.append(" executed in: ");
		result.append(milliseconds);
		result.append(" ms");
		
		return result.toString();
	}
	
	private static String shortenQuery(String query) {
		query = query.replaceAll("^\\s*", "");
		if(query.length() > 20) 
			return query.substring(0, 20) + "...";
		else
			return query;
	}

}
