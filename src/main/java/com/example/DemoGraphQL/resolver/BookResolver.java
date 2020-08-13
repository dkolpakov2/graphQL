package com.example.DemoGraphQL.resolver;

import java.util.List;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.example.DemoGraphQL.model.Author;
import com.example.DemoGraphQL.model.Book;
import com.example.DemoGraphQL.repository.AuthorRepository;
import com.example.DemoGraphQL.repository.BookRepository;

public class BookResolver implements GraphQLResolver<Book> {
    private AuthorRepository authorRepository;
    private BookRepository bookRepository;

    public BookResolver(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Iterable<Book> getBooks(BookRepository bookRepository) {
        return bookRepository.findAll();
    }
    
    public Author getAuthor(Book book) {
        return authorRepository.findOne(book.getAuthor().getId());
    }
}
