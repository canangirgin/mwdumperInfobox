/*
 * MediaWiki import/export processing tools
 * Copyright 2005 by Brion Vibber
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * $Id$
 */

package org.mediawiki.importer;

import org.mediawiki.extractor.Infobox;

import java.util.*;

public class Revision {
	public int Id;
	public Calendar Timestamp;
	public Contributor Contributor;
	public String Comment;
	public String Text;
	public boolean Minor;
    //Canan
    public Infobox InfoBox;



	public boolean isRedirect() {
		// FIXME
		return Text.startsWith("#REDIRECT ") || Text.startsWith("#redirect ");
	}
	
	public Revision() {
		Comment = "";
		Text = "";
        InfoBox = null;
   		Minor = false;

	}
    /*
    public void extractPattern() {
        String p_ad=""+InfoBox.Propetys.get("p_ad");
        if (!p_ad.isEmpty() & !p_ad.equals("null"))
            {
        Set entries = InfoBox.Propetys.entrySet();
        Iterator entriesIterator = entries.iterator();
        while(entriesIterator.hasNext()){
            Map.Entry mapping = (Map.Entry) entriesIterator.next();
           if (!(""+mapping.getKey()).equals("p_ad"))
            Patterns.put(""+mapping.getKey(), findPattern(p_ad,""+mapping.getValue()));
        }
                }
    }
     //TODO canan burayı iyi duşunmak lazım
    private String findPattern(String name, String value) {
        /*
         if (!value.isEmpty()& !value.equals("null"))

             {
                 int valueIndex= Text.indexOf(value);
                  int keyIndex=-1;

                  if (valueIndex>-1)
                      {
                       int keyIndexSonra = Text.indexOf(name,valueIndex);
                       int keyIndexOnce = Text.split(value)[0].lastIndexOf(name);
                      if (Math.abs(valueIndex-keyIndexOnce)<Math.abs(valueIndex-keyIndexSonra))
                      {
                          keyIndex=  keyIndexOnce;
                      }
                      }

               if (valueIndex>keyIndex)
                   {if (keyIndex<20)keyIndex=0;else keyIndex=keyIndex -20;
                   if (valueIndex+20>Text.length())valueIndex=Text.length();else valueIndex=valueIndex +20;
                 return Text.substring(keyIndex,valueIndex);
                       }
               else {
                 if (valueIndex<20)valueIndex=0;else valueIndex=valueIndex -20;
                 if (keyIndex+20>Text.length())keyIndex=Text.length();else keyIndex=keyIndex +20;
                 return Text.substring(valueIndex,keyIndex);
                 }
   }
        return null;
}    */
}
