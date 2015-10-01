package edu.tum.uc.jvm.utility.eval;

public class CSVStringBuilder {

    private StringBuilder sb = new StringBuilder();
    
    public CSVStringBuilder reset() {
	sb = new StringBuilder();
	return this;
    }
    
    public CSVStringBuilder newLine() {
	sb.append("\n");
	return this;
    }
    
    public CSVStringBuilder append(String s) {
	sb.append("\"");
	sb.append(s);
	sb.append("\"");
	sb.append(";");
	return this;
    }
    
    public String toString() {
	return sb.toString();
    }
    
}
