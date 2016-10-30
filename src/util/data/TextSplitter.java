package util.data;

import java.io.File;

public class TextSplitter extends BigDataSplitter<TextList> {
	public TextSplitter() {
		super();
	}
	public TextSplitter(String input, String output) {
		super(input, output, new TextList());
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
