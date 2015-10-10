package com.letspro.core.api;

import static org.mongodb.morphia.aggregation.Projection.projection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.mongodb.MongoClient;

public class AggregationTest {
    
    public Datastore getDs() {
        final Morphia morphia = new Morphia();
        morphia.mapPackage("com.letspro.commons.domain.mongodb");
        String host = "localhost";
        int port = 27017;
        String db = "core";
        MongoClient client = new MongoClient(host, port);
        Datastore datastore = morphia.createDatastore(client, db);
        datastore.ensureIndexes();
        return datastore;
    }
    
    @Test
    public void testUnwind() throws ParseException {
        /*SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        getDs().save(new User("jane", format.parse("2011-03-02"), "golf", "racquetball"),
                     new User("joe", format.parse("2012-07-02"), "tennis", "golf", "swimming"));

        Iterator<User> aggregate = getDs().createAggregation(User.class)
                                          .project(projection("_id").suppress(), projection("name"), projection("joined"),
                                                   projection("likes"))
                                          .unwind("likes")
                                          .aggregate(User.class);
        int count = 0;
        while (aggregate.hasNext()) {
            User user = aggregate.next();
            switch (count) {
                case 0:
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("golf", user.likes.get(0).getLike());
                    break;
                case 1:
                    Assert.assertEquals("jane", user.name);
                    Assert.assertEquals("racquetball", user.likes.get(0).getLike());
                    break;
                case 2:
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("tennis", user.likes.get(0).getLike());
                    break;
                case 3:
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("golf", user.likes.get(0).getLike());
                    break;
                case 4:
                    Assert.assertEquals("joe", user.name);
                    Assert.assertEquals("swimming", user.likes.get(0).getLike());
                    break;
                default:
                    Assert.fail("Should only find 5 elements");
            }
            count++;
        }*/
    }
    
    private static final class Like {
        private String like;
        
        private Like() {};
        
        private Like(String like) {
            this.like = like;
        }
        
        public String getLike() {
            return this.like;
        }
        @Override
        public String toString() {
            return String.format("%s", this.like);
        }
    }

    @Entity(value = "books", noClassnameStored = true)
    private static final class Book {
        @Id
        private ObjectId id;
        private String title;
        private String author;
        private Integer copies;
        private List<String> tags;

        private Book() {
        }

        private Book(final String title, final String author, final Integer copies, final String... tags) {
            this.title = title;
            this.author = author;
            this.copies = copies;
            this.tags = Arrays.asList(tags);
        }

        @Override
        public String toString() {
            return String.format("Book{title='%s', author='%s', copies=%d, tags=%s}", title, author, copies, tags);
        }
    }

    @Entity("authors")
    private static class Author {
        @Id
        private String name;
        private List<String> books;
    }

    @Entity("users")
    private static final class User {
        @Id
        private ObjectId id;
        private String name;
        private Date joined;
        private List<Like> likes;

        private User() {
        }

        private User(final String name, final Date joined, final String... likes) {
            this.name = name;
            this.joined = joined;
            this.likes = new ArrayList<Like>();
            for (String l : likes) {
                this.likes.add(new Like(l));
            }
        }

        @Override
        public String toString() {
            return String.format("User{name='%s', joined=%s, likes=%s}", name, joined, likes);
        }
    }

    @Entity
    private static class CountResult {

        @Id
        private String author;
        private int count;

        public String getAuthor() {
            return author;
        }

        public int getCount() {
            return count;
        }
    }

}
