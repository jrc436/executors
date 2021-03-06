package util.data.maps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import util.data.corpus.Comment;
import util.data.corpus.CommentFormat;
import util.data.json.JsonReadable;
import util.sys.DataType;

public class KeywordList extends DataCollection<Comment> {
	private static final long serialVersionUID = 6441745759936713041L;
	public KeywordList() { // dummy constructor
		super();
	}
	public KeywordList(String[] keywords, CommentFormat cf) {
		super(keywords);
		this.cf = cf;
	}
	public KeywordList(KeywordList kl) {
		super(kl);
		this.cf = kl.cf;
	}
	public KeywordList(List<String> fileLines, CommentFormat cf) {
		super(fileLines, cf);
	}
	@Override
	public boolean hasNArgs() {
		return true;
	}
	public static KeywordList createFromFile(File f, CommentFormat cf) {
		List<String> lines = null;;
		try {
			lines = Files.readAllLines(f.toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		String thisCreateLine = lines.get(0);
		if (!isKeyLine(thisCreateLine)) {
			System.err.println("These lists have still not been 'fixed', please run ReorderListProcessor");
			throw new IllegalArgumentException();
		}
		return new KeywordList(lines, cf);
	}

	@Override
	public String getConstructionErrorMsg() {
		return "KeywordList requires one or more keywords";
	}
	@Override
	public DataType deepCopy() {
		return new KeywordList(this);
	}
	@Override
	protected Collection<Comment> getEmptyCollection() {
		return new ArrayList<Comment>();
	}
	@Override
	protected Comment getValue(Comment c) {
		return c;
	}
	public List<String> getRelevantKeywords(String text) {		
		List<String> retval = new ArrayList<String>();
		if (text.isEmpty()) {
			return retval;
		}
		Set<String> words = new HashSet<String>(Arrays.asList(text.split("\\s+")));
		for (String key : this.keySet()) {
			if (words.contains(key)) {
				retval.add(key);
			}
		}
		return retval;
	}
	@Override
	protected Comment parseValue(String s) {
		JsonReadable jr = JsonReadable.fromString(s);
		return cf.getComment(jr);
	}


}
