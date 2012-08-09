package org.crawlgorithm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {

	private static final Set<String> setOfUrls = new HashSet<String>();
	private static final int LIMIT = 100;
	private static final Logger logger = LoggerFactory
			.getLogger(WebCrawler.class);

	public static void main(String[] args) {
		// initial URL
		String urlName = "http://news.ycombinator.com";
		try {
			processUrl(urlName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processUrl(String urlName) throws Exception {

		// parse current page and get all URLs from it
		List<String> currentPageUrls = readPage(urlName);

		// check if unique
		for (String url : currentPageUrls) {
			// can ths be added to current List?
			if (setOfUrls.add(url)) {
				// call processURL to process each of these URLs
				// this will make it depth-first
				if (setOfUrls.size() <= LIMIT && checkDuplicate(url)) {
					logger.info("processing URL#: " + setOfUrls.size() + " - "
							+ url);
					processUrl(url);
				} else {
					break;
				}
			}
		}
	}

	// check if the pattern is present in existing records
	private static boolean checkDuplicate(String pattern) {

		Pattern p = Pattern.compile(pattern);

		for (String element : setOfUrls) {
			if (p.matcher(element).matches())
				return true;
		}
		return false;
	}

	// collect the URLs here, store in the returnList and send back
	private static List<String> readPage(String urlName) throws Exception {

		List<String> returnList = new ArrayList<String>();

		try {
			URL url = new URL(urlName);
			URLConnection connection = url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String inputLine = in.readLine();
			String regexp = "href=[\\'\"]?([http][^\'\" >]+)";
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = null; // pattern.matcher(inputLine);
			StringBuilder builder = null;

			while ((inputLine = in.readLine()) != null) {
				matcher = pattern.matcher(inputLine);

				if (matcher.find() && !(matcher.group().contains(".css"))
						&& !(matcher.group().contains(".ico"))
						&& !(matcher.group().contains(".exe"))) {

					int len = matcher.group().length() - 1;
					if (matcher.group().charAt(len) == '/') {
						builder = new StringBuilder(matcher.group());
						returnList.add(builder.toString().trim());
					} else {
						returnList.add(matcher.group().substring(6).trim());
					}
				}
			}
		} catch (Exception e) {
		}

		// either full or null
		return returnList;
	}
}