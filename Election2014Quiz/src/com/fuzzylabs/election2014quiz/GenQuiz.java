package com.fuzzylabs.election2014quiz;

public class GenQuiz {
	public int questionId;
	public String question;
	public String[] options;

	public static String QUIZ_TYPE_1 = "1";
	public static String QUIZ_TYPE_2 = "2";
	public static String QUIZ_TYPE_3 = "3";
	public static String TYPE_ID = "typeId";
	public static String DB_SET = "dbSet";
	public static String VOTED = "voted";

	public GenQuiz() {
		this.questionId = 0;
		this.question = "";
		options = new String[] { "", "", "", "" };
	}
}
