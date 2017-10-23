package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.crypto.Data;

@Configuration
public class DatabaseConfiguration {

    @Bean
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {

        return DataSourceBuilder.create().build();
    }

    private HikariDataSource createHiKariDatasource(DataSource ds) {

        HikariConfig hc = new HikariConfig();
        hc.setDataSource(ds);

        HikariDataSource hd = new HikariDataSource(hc);
        return hd;
    }

    @Bean
    HibernateJpaVendorAdapter getHibernateAdaptor() {

        HibernateJpaVendorAdapter ha=new HibernateJpaVendorAdapter();
        ha.setDatabase(Database.MYSQL);
        ha.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        ha.setGenerateDdl(true);

        return ha;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter ha) {

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(createHiKariDatasource(albumsDataSource));
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(ha);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.albums");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("albums-unit");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean moviesEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter ha) {

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(createHiKariDatasource(moviesDataSource));
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(ha);
        localContainerEntityManagerFactoryBean.setPackagesToScan("org.superbiz.moviefun.movies");
        localContainerEntityManagerFactoryBean.setPersistenceUnitName("movies-unit");

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    PlatformTransactionManager albumsPlatformTransactionManager(
             @Qualifier("albumsEntityManagerFactoryBean") EntityManagerFactory albumsEntityManagerFactoryBean) {

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(albumsEntityManagerFactoryBean);

        return jpaTransactionManager;
    }

    @Bean
    PlatformTransactionManager moviesPlatformTransactionManager(
            @Qualifier("moviesEntityManagerFactoryBean") EntityManagerFactory moviesEntityManagerFactoryBean) {

        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(moviesEntityManagerFactoryBean);

        return jpaTransactionManager;
    }
}
