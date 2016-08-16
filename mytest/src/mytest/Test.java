package mytest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.file.FileSystemException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import javax.net.ssl.SSLHandshakeException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
	public static void main(String[] args) throws Exception {
		
		Test t = new Test();
		
		boolean flag = false;
		String query = URLEncoder.encode("jsoup", "UTF-8");
		Date curDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String myCurDate = sdf.format(curDate);
		int pageNo = 121;
		int num = 0;
		int order = 0;
//		String url = "https://search.naver.com/search.naver?where=post&sm=tab_jum&ie=utf8&query=" + query
//				+ "&date_from=20030520&date_to=" + myCurDate + "&date_option=4&start=" + pageNo;
		String url = "https://search.naver.com/search.naver?where=post&sm=tab_jum&ie=utf8&query=" + query
				+ "&start=" + pageNo;
		String[] urlArr = new String[10];
		String[] array = new String[2];

		BufferedWriter out = new BufferedWriter(new FileWriter("C:/Users/stu15/Desktop/hc/test.txt"));

		Document forTotal = Jsoup.connect(url).get();
		Elements totalSearched = forTotal.select(".title_num");
		array = totalSearched.get(0).text().split("/");
		int totalNum = t.getTotalSearched(array[1].trim());
		System.out.println((totalNum/10)*10+1);
		
		while (pageNo != ((totalNum/10)*10+1)) {
			try {
				new Thread();
				Thread.sleep(100);
				Document doc = Jsoup.connect("https://search.naver.com/search.naver?where=post&sm=tab_jum&ie=utf8&query=" + query
						+ "&start=" + pageNo).get();
				
				Elements liList = doc.select("#elThumbnailResultArea li");
				for (Element e : liList) {
					order++;
					String blogUrl = e.select("a").attr("href");
					// urlArr.add(blogUrl);
					urlArr[num] = blogUrl;
					System.out.println(order + "번째: " + blogUrl);
					num++;
				}

				for (String str : urlArr) {
					if(str == null) {
						flag = true;
						break;
					}
					
					if (str.contains("blog.naver.com")){
						new Thread();
						Thread.sleep(100);
						Document doc2 = Jsoup.connect(str).get();
						Elements list = doc2.select("frame");
						for (Element e2 : list) {
							if (e2.attr("id").equals("mainFrame")) {
								// System.out.println("mainFrame" + e2.attr("src"));
								Document doc3 = Jsoup.connect("http://blog.naver.com" + e2.attr("src")).get();
								Elements list2 = doc3.select("#postListBody");
								new Thread();
								Thread.sleep(100);
								for (Element e3 : list2) {
									out.write(e3.text());
									out.newLine();
									new Thread();
									Thread.sleep(100);

									// System.out.println(e3.text());
								}

							} else if (e2.attr("id").equals("screenFrame")) {
								new Thread();
								Thread.sleep(100);
								Document doc4 = Jsoup.connect(e2.attr("src")).get();
								Elements list3 = doc4.select("#mainFrame");
								for (Element e3 : list3) {
									// System.out.println("screenFrame" +
									// e3.attr("src"));
									new Thread();
									Thread.sleep(100);
									Document doc5 = Jsoup.connect("http://blog.naver.com" + e3.attr("src")).get();
									Elements list4 = doc5.select("#postListBody");
									for (Element e4 : list4) {
										out.write(e4.text());
										out.newLine();
										new Thread();
										Thread.sleep(100);

										// System.out.println(e4.text());
									}
								}
							}
						}
					} else {
						Document doc6 = Jsoup.connect(str).get();
						out.write(doc6.text());
						out.newLine();
						new Thread();
						Thread.sleep(100);
					}
					
				}
				if(flag) break;
				
			} catch (FileSystemException fe) {
				System.out.println("file error");
			} catch (SocketTimeoutException se) {
				System.out.println("timeout error");
			} catch (HttpStatusException hse) {
				System.out.println("404 error at " + hse.getUrl());
			} catch (SSLHandshakeException sse) {
				System.out.println("SSLHandshakeException: " + sse.getMessage());
			}
			num = 0;
			pageNo += 10;
		}
		System.out.println(array[1].trim());
		out.close();
	}
	
	public int getTotalSearched(String str) {
		
		String total = "";
		int totalLength = 0;
		int totalSearched = 0;
		StringTokenizer st = new StringTokenizer(str, ",");
		ArrayList<String> strArr = new ArrayList<>();
		while(st.hasMoreTokens()) {
			String temp = st.nextToken();
			strArr.add(temp);
		}
		for (String s : strArr) {
			totalLength += s.length();
			total += s;
		}
		totalSearched = Integer.valueOf(total.substring(0, totalLength - 1));
		
		return totalSearched;
	}
}
