package com.example.mobileannotatorapp;


public class Data {
    private String dateTime;
    private String batteryLevel;
    private String typeOfPrompt;
    private String fileName;
    private String recognizedText;
    private String audioLength;
    private String[] meaningfulKeywords;
    public Data(String dateTime, String batteryLevel, String typeOfPrompt, String fileName, String recognizedText, String audioLength, String[] meaningfulKeywords) {
        this.dateTime = dateTime;
        this.batteryLevel = batteryLevel;
        this.typeOfPrompt = typeOfPrompt;
        this.fileName = fileName;
        this.recognizedText = recognizedText;
        this.audioLength = audioLength;
        this.meaningfulKeywords = meaningfulKeywords;

    }

    public String[] getData() {
        String keywords = "";

        for (String word: meaningfulKeywords) {
            keywords = keywords + word + "; ";
        }
        int keywordsLen = keywords.length();
        if (keywordsLen >= 2) {
            keywords = keywords.substring(0, keywordsLen - 2);//problem?
        }
        String[] data = new String[] {dateTime, batteryLevel, typeOfPrompt, fileName, recognizedText, audioLength, keywords};
        return data;
    }
}
