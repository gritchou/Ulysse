/*
 * Qualipso Funky Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation. See the GNU
 * Lesser General Public License in LGPL.txt for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 */
 package org.qualipso.factory.client.test.matcher;
/**
*@author Benjamin Dreux benjiiiiii@gmail.com
**/
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.qualipso.factory.indexing.SearchResult;
import org.qualipso.factory.FactoryResourceIdentifier;


public class SearchResultWithFactoryResourceIdentfier extends TypeSafeMatcher<SearchResult> {
  private FactoryResourceIdentifier fri;
	
  public SearchResultWithFactoryResourceIdentfier(FactoryResourceIdentifier fri){
  this.fri = fri;
  }
  @Override
  public boolean matchesSafely(SearchResult sr) {
    return sr.getFactoryResourceIdentifier().equals(fri);
  }

  public void describeTo(Description description) {
    description.appendText("a SearchResult with a FactoryResourceIdentifier equals to ").appendValue(fri);
  }

  @Factory
  public static <T> Matcher<SearchResult> searchResultWithFactoryResourceIdentifier(FactoryResourceIdentifier fri) {
    return new SearchResultWithFactoryResourceIdentfier(fri);
  }

}
