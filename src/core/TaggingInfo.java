package core;

public class TaggingInfo {
	private String header = null;
	private String mostLikelyHeader = null;
	private String tag = null;
	private double similarty = 0;
	private boolean isStayEmpty = true;
	private String belongExcel = null;
	private int belongSheet = 0;

	
	public TaggingInfo(String excel, int sheetIndex, String header, String mostLikelyHeader, String tag, double similarty, boolean isStayEmpty) {
		this.belongExcel = excel;
		this.belongSheet = sheetIndex;
		this.header = header;
		this.mostLikelyHeader = mostLikelyHeader;
		this.tag = tag;	
		this.similarty = similarty;
		this.isStayEmpty = isStayEmpty;
	}
	
	public boolean getIsStayEmpty() {
		return isStayEmpty;
	}

	public void setStayEmpty(boolean isStayEmpty) {
		this.isStayEmpty = isStayEmpty;
	}

	public String getMostLikelyHeader() {
		return mostLikelyHeader;
	}

	public void setMostLikelyHeader(String mostLikelyHeader) {
		this.mostLikelyHeader = mostLikelyHeader;
	}
	
	public double getSimilarty() {
		return similarty;
	}

	public void setSimilarty(double similarty) {
		this.similarty = similarty;
	}

	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getBelongExcel() {
		return belongExcel;
	}
	public void setBelongExcel(String belongExcel) {
		this.belongExcel = belongExcel;
	}
	public int getBelongSheet() {
		return belongSheet;
	}
	public void setBelongSheet(int belongSheet) {
		this.belongSheet = belongSheet;
	}
	
}
