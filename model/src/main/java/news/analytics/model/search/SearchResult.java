package news.analytics.model.search;

/**
 * Represents a single search record.
 */
public class SearchResult {
    private String uri;
    private String newsAgency;
    private String section;
    private String title;
    private String city;
    private Long publishDate;
    private Float sentimentScore;

    public SearchResult(String uri, String newsAgency, String section, String title, String city, Long publishDate, Float sentimentScore) {
		super();
		this.uri = uri;
		this.newsAgency = newsAgency;
		this.section = section;
		this.title = title;
		this.city = city;
		this.publishDate = publishDate;
		this.sentimentScore = sentimentScore;
	}

	public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNewsAgency() {
        return newsAgency;
    }

    public void setNewsAgency(String newsAgency) {
        this.newsAgency = newsAgency;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Long publishDate) {
        this.publishDate = publishDate;
    }

    public Float getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Float sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

	@Override
	public String toString() {
		return "SearchResult [uri=" + uri + ", newsAgency=" + newsAgency + ", section=" + section + ", title=" + title
				+ ", city=" + city + ", publishDate=" + publishDate + ", sentimentScore=" + sentimentScore + "]";
	}
    
    
}
