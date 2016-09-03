package application;

public class GlobalFlags {
	private static GlobalFlags instance;
	public static GlobalFlags getInstance () {
		if (GlobalFlags.instance == null) {
			GlobalFlags.instance = new GlobalFlags();
		}
		return GlobalFlags.instance;
	}
	private boolean HTMLWorking;
	private boolean parserWorking;
	private boolean viewUpdaterWorking;

	private GlobalFlags(){}

	public boolean isHTMLWorking() {
		return HTMLWorking;
	}

	public boolean isParserWorking() {
		return parserWorking;
	}

	public boolean isViewUpdaterWorking() {
		return viewUpdaterWorking;
	}

	public void setHTMLWorking(boolean hTMLWorking) {
		HTMLWorking = hTMLWorking;
	}

	public void setParserWorking(boolean parserWorking) {
		this.parserWorking = parserWorking;
	}

	public void setViewUpdaterWorking(boolean viewUpdaterWorking) {
		this.viewUpdaterWorking = viewUpdaterWorking;
	}



}
