package com.zerobase.stock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class StockApplication {

	public static void main(String[] args) {
		//SpringApplication.run(StockApplication.class, args);

		// https://jsoup.org/apidocs/에서 자세한 사용 볼 수 있음
		try {
			Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1691366400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
			Document document = connection.get();

			Elements eles = document.getElementsByAttributeValue("data-test", "historical-prices");
			Element ele = eles.get(0); // table 전체

			Element tbody = ele.children().get(1); // get(0) : thead, get(2) : tfoot
			for (Element e : tbody.children()) {
				String txt = e.text();
				if(!txt.endsWith("Dividend")) {
					continue;
				}
				String[] splits = txt.split(" ");
				String month = splits[0];
				int day = Integer.parseInt(splits[1].replace(",", ""));
				int year = Integer.parseInt(splits[2]);
				String dividend = splits[3];

				System.out.println(year + "/" + month + "/" + day + " -> " + dividend);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
