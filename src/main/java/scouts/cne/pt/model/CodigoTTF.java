package scouts.cne.pt.model;

/**
 * @author 62000465 2019-02-01
 *
 */
public class CodigoTTF
{
	private final String	strName;
	private final String	strUrl;

	/**
	 * constructor
	 * 
	 * @author 62000465 2019-02-01
	 * @param name
	 * @param url
	 */
	public CodigoTTF( String name, String url )
	{
		super();
		strName = name;
		strUrl = url;
	}

	/**
	 * Getter for name
	 * 
	 * @author 62000465 2019-02-01
	 * @return the name {@link String}
	 */
	public String getName()
	{
		return strName;
	}

	/**
	 * Getter for url
	 * 
	 * @author 62000465 2019-02-01
	 * @return the url {@link String}
	 */
	public String getUrl()
	{
		return strUrl;
	}
}
