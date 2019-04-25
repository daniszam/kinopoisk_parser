package ru.itis.darZam.parser;

import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.htmlcleaner.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import ru.itis.darZam.models.*;
import ru.itis.darZam.repositories.FilmRepository;
import ru.itis.darZam.repositories.FilmRepositoryImpl;
import ru.itis.darZam.repositories.RatingImdbRepositoryImpl;
import ru.itis.darZam.services.ActorSerivce;
import ru.itis.darZam.services.ComposerServices;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Parser {

    private HttpClient client;
    private static final String kinopoiskUrl = "https://www.kinopoisk.ru/film/%s/";
    private static final String kinopoiskCastUrl = "https://www.kinopoisk.ru/film/%s/cast/";
    private ActorSerivce actorSerivce;

    private ComposerServices composerServices;
    private RatingImdbRepositoryImpl ratingImdbRepository;

    private Logger logger = Logger.getLogger("Parser");

    public Parser(Session session) {
        this.client = HttpClients.createDefault();
        this.actorSerivce = new ActorSerivce(session);
        this.composerServices = new ComposerServices(session);
        this.ratingImdbRepository = new RatingImdbRepositoryImpl(session);
    }

    @SneakyThrows
    public Film parseFilm(Integer filmId) {

        URIBuilder uriBuilder = new URIBuilder(String.format(kinopoiskUrl, filmId));
        HttpGet get = new HttpGet(uriBuilder.build());
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String page = EntityUtils.toString(entity);
        Film film = this.buildFilmByHtmlPage(page);
        return film;
    }

    @SneakyThrows
    public Film setCastToFilm(Film film, Integer filmId){
        URIBuilder uriBuilder = new URIBuilder(String.format(kinopoiskCastUrl, filmId));
        HttpGet get = new HttpGet(uriBuilder.build());
        HttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        String page = EntityUtils.toString(entity);
        System.out.println(page);
        Set<Actor> actors = prepareActors(page);
        Set<Composer> composers = prepareComposers(page);
        film.setActors(actors);
        film.setComposers(composers);

        return film;
    }

    @SneakyThrows
    private Film buildFilmByHtmlPage(String page) {
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode tagNode = cleaner.clean(page);

//        System.out.println(page);
        Document doc = new DomSerializer(
                new CleanerProperties()).createDOM(tagNode);

        XPath xpath = XPathFactory.newInstance().newXPath();


        String name = (String) xpath.evaluate("//*[@id='headerFilm']/h1",
                doc, XPathConstants.STRING);
        String description = (String) xpath.evaluate("//*[@id='syn']/tbody/tr[1]/td/table/tbody/tr[1]/td/span/div",
                doc, XPathConstants.STRING);
        String tagLine = (String) xpath.evaluate("//*[@id='infoTable']/table/tbody/tr[3]/td[2]",
                doc, XPathConstants.STRING);

        String yearStr = (String) xpath.evaluate("//*[@id='infoTable']/table/tbody/tr[1]/td[2]/div/a",
                doc, XPathConstants.STRING);

        RatingImdb ratingImdb = null;
        try {
            String ratingIMDBCount = (String) xpath.evaluate("//*[@id='block_rating']/div[1]/div[2]",
                    doc, XPathConstants.STRING);
            ratingImdb = prepareRatingImbd(ratingIMDBCount);
        } catch (Exception e) {
            logger.info("No imdb rating");
        }

        String budgetStr = (String) xpath.evaluate("//*[@id='infoTable']/table/tbody/tr[12]/td[2]/div",
                doc, XPathConstants.STRING);

        Float dues = null;
        try {
            String duesStr = (String) xpath.evaluate("//*[@id='div_world_box_td2']/div/a[1]",
                    doc, XPathConstants.STRING);
            dues = prepareDues(duesStr);
        } catch (Exception e) {
            logger.info("No dues");
        }

        String premiere = (String) xpath.evaluate("//*[@id='div_rus_prem_td2']/div/span[1]/a[1]",
                doc, XPathConstants.STRING);

        String age = (String) xpath.evaluate("//*[@id='infoTable']/table/tbody/tr[16]/td[2]/span",
                doc, XPathConstants.STRING);

        String duration = (String) xpath.evaluate("//*[@id='runtime']", doc, XPathConstants.STRING);

        String positiveReview = (String) xpath.evaluate("//*[@id='syn']/tbody/tr[1]/td/table/tbody/tr[3]/td/div[1]/div/div[5]/span[1]",
                doc, XPathConstants.STRING);
        Integer positiveReviewCount = null;

        if (!positiveReview.replaceAll(" ", "").isEmpty()){
            positiveReviewCount = Integer.parseInt(positiveReview.replaceAll("\\D+", ""));
        }

        String negativeReview = (String) xpath.evaluate("//*[@id='syn']/tbody/tr[1]/td/table/tbody/tr[3]/td/div[1]/div/div[5]/span[2]",
                doc, XPathConstants.STRING);

        Integer negativeReviewCount = null;

        if (!negativeReview.replaceAll(" ", "").isEmpty()){
            negativeReviewCount = Integer.parseInt(positiveReview.replaceAll("\\D+", ""));
        }

        Film film = Film.builder()
                .name(name)
                .description(description)
                .tagline(tagLine)
                .year(Integer.parseInt(yearStr.replaceAll("\\D+", "")))
                .ratingImdb(ratingImdb)
                .budget(getBudgetInDollar(budgetStr))
                .dues(dues)
                .premiere(prepareDate(premiere))
                .age(Short.parseShort(age.replaceAll("\\D+", "")))
                .duration(Float.parseFloat(duration.split("/")[0].replaceAll("\\D+", "")))
                .positiveReview(positiveReviewCount)
                .negativeReview(negativeReviewCount)
                .build();


        return film;

    }

    @SneakyThrows
    public Set<Actor> prepareActors(String page){
        page = page.replaceAll("\n", "");

        System.out.println(page);
        Pattern pattern = Pattern.compile("(Актеры</div>).+(Продюсеры)");
        Matcher matcher = pattern.matcher(page);
        List<String> names = getNames(matcher);
        return actorSerivce.getOrCreate(names);
    }

    @SneakyThrows
    public Set<Composer> prepareComposers(String page){
        page = page.replaceAll("\n", "");

        System.out.println(page);
        Pattern pattern = Pattern.compile("(Сценаристы</div>).+(Операторы)");
        Matcher matcher = pattern.matcher(page);
        List<String> names = getNames(matcher);
        return composerServices.getOrCreate(names);

    }

    private List<String> getNames(Matcher matcher){
        List<String> names = new ArrayList<>();
        while (matcher.find()) {
            System.out.println(matcher.group());
            Pattern namePattern = Pattern.compile("(<div class=\"name\"><a href=\"/name/\\d+/\">)[А-Яа-я ,]+(</a>)");
            Matcher nameMatcher = namePattern.matcher(matcher.group());
            while (nameMatcher.find()){
                String div = nameMatcher.group();
                String name = div.substring(div.indexOf("/\">")+3, div.indexOf("</a>"));
                names.add(name);
            }
        }

        return names;
    }

    private RatingImdb prepareRatingImbd(String ratingImdb) {
        String[] ratingAndCount = ratingImdb.split("\\(");

        RatingImdb rating = RatingImdb.builder()
                .count(Integer.parseInt(ratingAndCount[0].replaceAll("\\D+", "")))
                .estimation(Float.parseFloat(ratingAndCount[1].replaceAll("[^\\d.]+|\\.(?!\\d)", "")))
                .build();
        ratingImdbRepository.save(rating);
        return rating;

    }

    private Float prepareDues(String duseStr){
        String[] dues = duseStr.split("=");
        return this.getBudgetInDollar(dues[dues.length-1]);
    }

    @SneakyThrows
    private Date prepareDate(String dateStr){
        String[] date = dateStr.split(" ");
        Month dateMonth = null;
        for (Month month: Month.values()){
            if (month.getName().equals(date[1])){
                dateMonth = month;
            }
        }

        dateStr = dateStr.replaceAll(dateMonth.getName(), dateMonth.getNum().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd M yyyy");
        System.out.println(dateStr);
        java.util.Date date1 = simpleDateFormat.parse(dateStr);
        System.out.println(date1);
        return new Date(date1.getTime());
    }

    private Float getBudgetInDollar(String budgetStr) {
        float budget = Float.parseFloat(budgetStr.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
        if (budgetStr.contains("руб")) {
            budget = budget / 65;
        } else if (budgetStr.contains("€")) {
            budget = budget * 1.12f;
        }
        return budget;
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session =sessionFactory.openSession();

        Parser parser = new Parser(session);
        Film film = parser.parseFilm(1111102);
        film = parser.setCastToFilm(film,1111102);
        System.out.println(film);
        FilmRepositoryImpl filmRepository = new FilmRepositoryImpl(session);

        filmRepository.save(film);
    }

}

enum Month{
    JANUAR("января", 1),
    FEBRUAR("февраля",2),
    MARTH("марта", 3),
    APRIL("апреля", 4),
    MAY("мая", 5),
    JUNE("июня", 6),
    JULE("июля", 7),
    AUGUST("августа", 8),
    SEPTEMBER("сентября", 9),
    OKTOBER("октября", 10),
    NOVEMBER("ноября", 11),
    DECEMBER("декабря", 12);

    public String getName() {
        return name;
    }

    public Integer getNum() {
        return num;
    }

    private String name;
    private Integer num;

    private Month(String name, Integer num){
        this.name = name;
        this.num = num;
    }
}



