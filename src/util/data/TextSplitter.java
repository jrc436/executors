package util.data;

import java.io.File;

public class TextSplitter extends BigDataSplitter<TextList> {
	public TextSplitter() {
		super();
	}
	@Override
	public TextList getNextData() {
		File f = super.getNextFile();
		if (f == null) {
			return null;
		}
		return TextList.fromFile(f);
	}

}
