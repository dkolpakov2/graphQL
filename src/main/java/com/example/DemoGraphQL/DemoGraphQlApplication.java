package com.example.DemoGraphQL;

import com.example.DemoGraphQL.exception.GraphQLErrorAdapter;
import com.example.DemoGraphQL.model.Author;
import com.example.DemoGraphQL.model.Book;
import com.example.DemoGraphQL.repository.AuthorRepository;
import com.example.DemoGraphQL.repository.BookRepository;
import com.example.DemoGraphQL.resolver.BookResolver;
import com.example.DemoGraphQL.resolver.Mutation;
import com.example.DemoGraphQL.resolver.Query;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.GraphQLErrorHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class DemoGraphQlApplication {

	public static void main(String[] args) {
		Map<String, Object> props = new HashMap<>();
		props.put("server.port", 8081);
		new SpringApplicationBuilder().sources(DemoGraphQlApplication.class)
		.properties(props)
		.run(args);
		//SpringApplication app = new SpringApplication(DemoGraphQlApplication.class);		
		//app.setEnvironment(environment);
		//app.run( args );
	}
	@Bean
	public GraphQLErrorHandler errorHandler() {
		return new GraphQLErrorHandler() {
			@Override
			public List<GraphQLError> processErrors(List<GraphQLError> errors) {
				List<GraphQLError> clientErrors = errors.stream()
						.filter(this::isClientError)
						.collect(Collectors.toList());

				List<GraphQLError> serverErrors = errors.stream()
						.filter(e -> !isClientError(e))
						.map(GraphQLErrorAdapter::new)
						.collect(Collectors.toList());

				List<GraphQLError> e = new ArrayList<>();
				e.addAll(clientErrors);
				e.addAll(serverErrors);
				return e;
			}

			protected boolean isClientError(GraphQLError error) {
				return !(error instanceof ExceptionWhileDataFetching || error instanceof Throwable);
			}
		};
	}

	@Bean
	public BookResolver authorResolver(AuthorRepository authorRepository) {
		return new BookResolver(authorRepository);
	}

	@Bean
	public Query query(AuthorRepository authorRepository, BookRepository bookRepository) {
		return new Query(authorRepository, bookRepository);
	}

	@Bean
	public Mutation mutation(AuthorRepository authorRepository, BookRepository bookRepository) {
		return new Mutation(authorRepository, bookRepository);
	}

	@Bean
	public CommandLineRunner demo(AuthorRepository authorRepository, BookRepository bookRepository) {
		return (args) -> {
			Author author = new Author("Rudra", "Singh");
			//Author author = new Author("Herbert", "Schildt", new Book("Java: Guide, Sixth Edition", "0071809252", 728));
			authorRepository.save(author);

			bookRepository.save(new Book("Java: Guide, Sixth Edition", "0071809252", 728, author));
			Author auth = new Author("Dmitry", "Kolpakov");
			authorRepository.save(auth);
			bookRepository.save(new Book("Dmitry's Great book", "10987456123", 155, auth));
			bookRepository.save(new Book("Second Great book", "10987456124", 10, auth));
		};
	}
}
