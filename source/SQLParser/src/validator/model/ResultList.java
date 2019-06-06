package validator.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class ResultList {

	@XmlElement(name = "resultados")
	Collection<Result> results;

	public ResultList() {
		this.results = new ArrayList<>();
	}

	public Collection<Result> getResults() {
		return results;
	}

	public void setResults(Collection<Result> results) {
		this.results = results;
	}

	public void addResult(Result result) {
		this.results.add(result);
	}

}
