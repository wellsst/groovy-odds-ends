package datetimes

import com.joestelmach.natty.*;

@Grab(group='com.joestelmach', module='natty', version='0.7')

Parser parser = new Parser();
List groups = parser.parse("the day before next thursday");
for(DateGroup group:groups) {
    List dates = group.getDates();
    int line = group.getLine();
    int column = group.getPosition();
    String matchingValue = group.getText();
    String syntaxTree = group.getSyntaxTree().toStringTree();
    Map parseMap = group.getParseLocations();
    boolean isRecurreing = group.isRecurring();
    Date recursUntil = group.getRecursUntil();
}

groups = parser.parse("Tuesday 20 February 1912");
for(DateGroup group:groups) {
    List dates = group.getDates();
    int line = group.getLine();
    int column = group.getPosition();
    String matchingValue = group.getText();
    String syntaxTree = group.getSyntaxTree().toStringTree();
    Map parseMap = group.getParseLocations();
    boolean isRecurreing = group.isRecurring();
    Date recursUntil = group.getRecursUntil();
}

groups = parser.parse("Thu Aug 01 2013 00:00:00 GMT+1000 (EST)");
for(DateGroup group:groups) {
    List dates = group.getDates();
    int line = group.getLine();
    int column = group.getPosition();
    String matchingValue = group.getText();
    String syntaxTree = group.getSyntaxTree().toStringTree();
    Map parseMap = group.getParseLocations();
    boolean isRecurreing = group.isRecurring();
    Date recursUntil = group.getRecursUntil();
}

groups = parser.parse("crap");
for(DateGroup group:groups) {
    List dates = group.getDates();
    int line = group.getLine();
    int column = group.getPosition();
    String matchingValue = group.getText();
    String syntaxTree = group.getSyntaxTree().toStringTree();
    Map parseMap = group.getParseLocations();
    boolean isRecurreing = group.isRecurring();
    Date recursUntil = group.getRecursUntil();
}