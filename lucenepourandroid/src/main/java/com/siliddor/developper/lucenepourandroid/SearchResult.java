package com.siliddor.developper.lucenepourandroid;


import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;

import java.util.List;

public class SearchResult {
    public final int totalHits;
    public final List<Documents> documents;
    final ScoreDoc lastScoreDoc;
    final Query query;
    final Sort sort;
    final HighlightingHelper highlightingHelper;
    int fragmentLength = HighlightingHelper.DEFAULT_FRAGMENT_LENGTH;

    SearchResult(int totalHits, List<Documents> documents,
                 ScoreDoc lastScoreDoc, Query query, Sort sort,
                 HighlightingHelper highlightingHelper) {
        this.totalHits = totalHits;
        this.documents = documents;
        this.lastScoreDoc = lastScoreDoc;
        this.query = query;
        this.sort = sort;
        this.highlightingHelper = highlightingHelper;
    }

    public boolean hasMore() {
        return lastScoreDoc != null;
    }

    public String getHighlightedTitle(Documents doc) {
        highlightingHelper.setFragmentLength(Integer.MAX_VALUE);
        highlightingHelper.setLineFeedHTMLEscape(false);
        return highlightingHelper.highlightOrOriginal(Indexer.TITLE_FIELD_NAME, doc.titre);
    }

    public String getHighlightedReview(Documents doc) {
        highlightingHelper.setFragmentLength(HighlightingHelper.DEFAULT_FRAGMENT_LENGTH);
        highlightingHelper.setLineFeedHTMLEscape(false);
        return highlightingHelper.highlightOrOriginal(Indexer.REVIEW_FIELD_NAME, doc.source);
    }


    public void setFragmentLength(int length) {
        fragmentLength = length;
    }
}
