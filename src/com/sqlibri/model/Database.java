package com.sqlibri.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sqlibri.util.PrettyStatus;

public class Database {

	private final String GET_ALL_TABLES = "SELECT name FROM sqlite_master WHERE type='table';";

	private File file;

	public File getFile() {
		return file;
	}

	public Database(File file) {
		this.file = file;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void connect() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.toString());
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drop() {
		file.delete();
	}

	public List<String> getAllTables() {

		List<String> tables = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file.toString());
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(GET_ALL_TABLES);) {
			while (resultSet.next()) {
				tables.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tables;
	}

	public QueryResult executeQuery(String query) throws SQLException, Exception {
		QueryResult queryResult = new QueryResult();

		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {

			connection = DriverManager.getConnection("jdbc:sqlite:" + file.toString());
			statement = connection.createStatement();

			long start = System.currentTimeMillis();
			if (isDML(query)) {
				resultSet = statement.executeQuery(query);
			} else {
				statement.executeUpdate(query);
			}
			long end = System.currentTimeMillis();

			queryResult.setExecutionInfo(PrettyStatus.success(query, (end - start)));

			if (resultSet == null)
				return queryResult;

			queryResult.setColumnNames(new ArrayList<String>());
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				queryResult.getColumnNames().add(resultSet.getMetaData().getColumnName(i));
			}

			queryResult.setTableData(new ArrayList<>());
			for (int row = 0; resultSet.next(); row++) {
				queryResult.getTableData().add(new ArrayList<String>());
				for (int column = 0; column < resultSet.getMetaData().getColumnCount(); column++) {
					queryResult.getTableData().get(row).add(resultSet.getString(column + 1));
				}
			}
		} finally {
			if (isDML(query)) {
				resultSet.close();
			}
			statement.close();
			connection.close();
		}

		return queryResult;
	}

	private boolean isDML(String query) {
		return query.toUpperCase().matches("^\\s*SELECT.*$");
	}

}
