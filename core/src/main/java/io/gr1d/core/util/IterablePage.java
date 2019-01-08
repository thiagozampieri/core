package io.gr1d.core.util;

import io.gr1d.core.model.Gr1dPage;

import java.math.BigDecimal;
import java.util.Iterator;

import static java.util.Optional.ofNullable;

public class IterablePage<T> implements Iterator<T>, Iterable<T> {

    private int currentIndex = 0;
    private Gr1dPage<T> page;
    private final PageRequester<T> requester;

    public IterablePage(final PageRequester<T> requester) {
        this.requester = requester;
    }

    @Override
    public boolean hasNext() {
        if (page == null) {
            page = requester.requestPage(ofNullable(page).map(p -> p.getPage() + 1).orElse(0));
            currentIndex = 0;
        }
        return hasNextInCurrentPage() || hasMorePages();
    }

    private boolean hasNextInCurrentPage() {
        return page.getContent().size() > currentIndex;
    }

    private boolean hasMorePages() {
        final int pages = BigDecimal.valueOf(page.getTotalElements())
                .divide(BigDecimal.valueOf(page.getSize()), BigDecimal.ROUND_UP)
                .intValue();
        return (page.getPage() + 1) < pages;
    }

    @Override
    public T next() {
        return hasNext() ? page.getContent().get(currentIndex++) : null;
    }

    @Override
    public Iterator<T> iterator() {
        return new IterablePage<>(requester);
    }
}
