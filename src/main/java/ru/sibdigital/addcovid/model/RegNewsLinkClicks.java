package ru.sibdigital.addcovid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "reg_news_link_clicks", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RegNewsLinkClicks {

    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "REG_NEWS_LINK_CLICKS_GEN", sequenceName = "reg_news_link_clicks_id_seq", allocationSize = 1, schema = "public")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REG_NEWS_LINK_CLICKS_GEN")
    private Long id;
    private String ip;
    private Timestamp time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "id_news", referencedColumnName = "id")
    private ClsNews news;
    public ClsNews getNews() {
        return news;
    }
    public void setNews(ClsNews news) {
        this.news = news;
    }

    @Basic
    @Column(name = "time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Basic
    @Column(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegNewsLinkClicks that = (RegNewsLinkClicks) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(news, that.news) &&
                Objects.equals(time, that.time) &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, news, time, ip);
    }

}