package news.analytics.parser.config;

/**
 * Represents a tag which contains information on RawNews.
 * In actual raw html content, HTML tag matching 'this' TagLocator will be found out.
 *
 * For example: if the given meta tag from raw html is as follows,
 * <meta name="keywords" content="Triple talaq case, Triple talaq bill, BJP"/>
 * Following TagLocator will represent above html tag
 * tagIdentifierTagName = meta
 * tagIdentifierAttributeName = name
 * tagIdentifierAttributeValue = keywords
 * valueAttributeName = content
 *
 * Similarly for html tag <meta charset="utf-8">, corresponding tag locator will look like:
 * tagIdentifierTagName = meta
 * tagIdentifierAttributeName = charset
 * valueAttributeName = charset
 */

public class TagLocator {
    /**
     * Name of an html tag
     */
    private String tagIdentifierTagName;

    /**
     * TagLocator with this Attribute will contain the valueAttributeName
     */
    private String tagIdentifierAttributeName;

    /**
     * TagLocator having attribute valueAttributeName = this valueAttributeName
     */
    private String tagIdentifierAttributeValue;

    /**
     * Value of this attribute is what we expect
     */
    private String valueAttributeName;


    public String getTagIdentifierTagName() {
        return tagIdentifierTagName;
    }

    public void setTagIdentifierTagName(String tagIdentifierTagName) {
        this.tagIdentifierTagName = tagIdentifierTagName;
    }

    public String getTagIdentifierAttributeName() {
        return tagIdentifierAttributeName;
    }

    public void setTagIdentifierAttributeName(String tagIdentifierAttributeName) {
        this.tagIdentifierAttributeName = tagIdentifierAttributeName;
    }

    public String getTagIdentifierAttributeValue() {
        return tagIdentifierAttributeValue;
    }

    public void setTagIdentifierAttributeValue(String tagIdentifierAttributeValue) {
        this.tagIdentifierAttributeValue = tagIdentifierAttributeValue;
    }

    public String getValueAttributeName() {
        return valueAttributeName;
    }

    public void setValueAttributeName(String valueAttributeName) {
        this.valueAttributeName = valueAttributeName;
    }
}

