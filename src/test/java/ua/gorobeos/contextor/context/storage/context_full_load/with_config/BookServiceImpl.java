package ua.gorobeos.contextor.context.storage.context_full_load.with_config;

import ua.gorobeos.contextor.context.annotations.Element;

@Element
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  public BookServiceImpl(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

}
