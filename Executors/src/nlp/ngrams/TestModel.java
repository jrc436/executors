package nlp.ngrams;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestModel {
	public static void main(String[] args) throws IOException {
		File f = new File ("/work/research/lang-production/srilm/test/sentences.lm");
		NgramModel nm = NgramModel.readFile(f.toPath());
		List<String> sentences = Files.readAllLines(Paths.get("/work/research/lang-production/srilm/test/test2.txt"));
		for (String sent : sentences) {
			System.out.println(sent+","+nm.logProb(sent));
		}
	}
}
