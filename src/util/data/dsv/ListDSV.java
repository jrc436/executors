package util.data.dsv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import filter.Filterable;
import util.sys.DataType;
import util.sys.FileWritable;
import java.util.regex.Pattern;


//delimiter separated values
public class ListDSV extends ArrayList<List<String>> implements Filterable<List<String>> {
	/**
	 * 
	 */
	public ListDSV() {
		super();
		this.headers = null;
		this.splitter = null;
	}
	//fill columns past where there's data with empty strings
	public ListDSV(String splitter, boolean fill) {
		super();
		this.splitter = splitter;
	}
	public ListDSV(String splitter) {
		this(splitter, true);
	}
	private static final long serialVersionUID = -4843965415284440893L;
	private List<String> headers;
	private final String splitter;
	public static ListDSV fromFile(File f, String splitter, boolean fill) throws IOException {
		splitter = unescapeJavaString(splitter);
		List<String> lines = Files.readAllLines(f.toPath());
		if (lines.isEmpty()) {
			throw new IllegalArgumentException(f + " doesn't contain any data");
		}
		String headers = lines.get(0);
		List<String> heads = new ArrayList<String>();
		String litsplit = Pattern.quote(splitter);
		//String litsplit = splitter;
		for (String s : headers.split(litsplit)) {
			heads.add(s);
		}
		if (heads.size() <= 1) {
			System.err.println("WARNING: only one header detected.");
			System.err.println(headers);
			System.err.println("Delimiter is: "+litsplit+".");
		}
		ListDSV toret = new ListDSV(heads, splitter);
		for (int i = 1; i < lines.size(); i++) {
			toret.add(new ArrayList<String>());
			for (String s : lines.get(i).split(litsplit)) {
				toret.get(i-1).add(s);
			}
			if (toret.get(i-1).size() != heads.size() && !fill) {
				System.err.println("Missing or extra data around line: "+i+"; line: "+lines.get(i));
				throw new IllegalArgumentException();
			}
			else if (toret.get(i-1).size() < heads.size()) {
				for (int j = toret.get(i-1).size(); j < heads.size(); j++) {
					toret.get(i-1).add("");
				}
			}
		}
		return toret;
	}
	private ListDSV(List<String> heads, String splitter) {
		super();
		this.headers = heads;
		this.splitter = splitter;
	}
	private ListDSV(ListDSV other) {
		super();
		this.headers = other.headers == null ? null : new ArrayList<String>(other.headers);
		this.splitter = other.splitter;
		for (List<String> data : other) {
			super.add(new ArrayList<String>(data));
		}
	}
	public void absorb(ListDSV other) {
		if (this.headers == null) {
			this.headers = new ArrayList<String>();
			this.headers.addAll(other.headers);
		}
		else if (this.headers.size() != other.headers.size()) {
			throw new IllegalArgumentException("Non-header aligned CSVs cannot be filtered using this system");
		}
		this.addAll(other);
	}
	public int getIndexForHeader(String header) {
		for (int i = 0; i < headers.size(); i++) {
			if (headers.get(i).equals(header)) {
				return i;
			}
		}
		System.err.println("WARN: Header: "+header+" was not found.");
		System.err.println("All Headers: "+headers);
		return -1;
	}
	
	@Override
	public int getNumFixedArgs() {
		return 0;
	}

	@Override
	public boolean hasNArgs() {
		return false;
	}

	@Override
	public String getConstructionErrorMsg() {
		return "ListCSV requires no args";
	}

	@Override
	public String getFileExt() {
		return ".csv";
	}

	@Override
	public ArrayList<String> getDataWriteLines() {
		ArrayList<String> ret = new ArrayList<String>();
		for (List<String> data : this) {
			ret.add(buildCSVString(data));
		}
		return ret;
	}

	@Override
	public String getHeaderLine() {
		return buildCSVString(headers);
	}

	@Override
	public String getFooterLine() {
		return null;
	}

	@Override
	public Iterator<String> getStringIter() {
		return FileWritable.<List<String>, ListDSV>iterBuilder(this, this::buildCSVString);
	}
	private String buildCSVString(List<String> string) {
		String add = "";
		for (String s : string) {
			add += s + splitter;
		}
		return add.substring(0, add.length()-splitter.length());
	}

	@Override
	public DataType deepCopy() {
		return new ListDSV(this);
	}
	@Override
	public void destroy(List<String> el) {
		super.remove(el);
	}
	/**
	 * Unescapes a string that contains standard Java escape sequences.
	 * <ul>
	 * <li><strong>&#92;b &#92;f &#92;n &#92;r &#92;t &#92;" &#92;'</strong> :
	 * BS, FF, NL, CR, TAB, double and single quote.</li>
	 * <li><strong>&#92;X &#92;XX &#92;XXX</strong> : Octal character
	 * specification (0 - 377, 0x00 - 0xFF).</li>
	 * <li><strong>&#92;uXXXX</strong> : Hexadecimal based Unicode character.</li>
	 * </ul>
	 * 
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	public static String unescapeJavaString(String st) {
		if (st == null) {
			return null;
		}
	    StringBuilder sb = new StringBuilder(st.length());

	    for (int i = 0; i < st.length(); i++) {
	        char ch = st.charAt(i);
	        if (ch == '\\') {
	            char nextChar = (i == st.length() - 1) ? '\\' : st
	                    .charAt(i + 1);
	            // Octal escape?
	            if (nextChar >= '0' && nextChar <= '7') {
	                String code = "" + nextChar;
	                i++;
	                if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                        && st.charAt(i + 1) <= '7') {
	                    code += st.charAt(i + 1);
	                    i++;
	                    if ((i < st.length() - 1) && st.charAt(i + 1) >= '0'
	                            && st.charAt(i + 1) <= '7') {
	                        code += st.charAt(i + 1);
	                        i++;
	                    }
	                }
	                sb.append((char) Integer.parseInt(code, 8));
	                continue;
	            }
	            switch (nextChar) {
	            case '\\':
	                ch = '\\';
	                break;
	            case 'b':
	                ch = '\b';
	                break;
	            case 'f':
	                ch = '\f';
	                break;
	            case 'n':
	                ch = '\n';
	                break;
	            case 'r':
	                ch = '\r';
	                break;
	            case 't':
	                ch = '\t';
	                break;
	            case '\"':
	                ch = '\"';
	                break;
	            case '\'':
	                ch = '\'';
	                break;
	            // Hex Unicode: u????
	            case 'u':
	                if (i >= st.length() - 5) {
	                    ch = 'u';
	                    break;
	                }
	                int code = Integer.parseInt(
	                        "" + st.charAt(i + 2) + st.charAt(i + 3)
	                                + st.charAt(i + 4) + st.charAt(i + 5), 16);
	                sb.append(Character.toChars(code));
	                i += 5;
	                continue;
	            }
	            i++;
	        }
	        sb.append(ch);
	    }
	    return sb.toString();
	}

}
