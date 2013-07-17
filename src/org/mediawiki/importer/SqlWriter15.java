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

// Doesn't actually work yet...

package org.mediawiki.importer;

import org.mediawiki.extractor.Infobox;
import org.mediawiki.extractor.Template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SqlWriter15 extends SqlWriter {
	private Page currentPage;
	private Revision lastRevision;
	
	public SqlWriter15(SqlWriter.Traits tr, SqlStream output) {
		super(tr, output);
	}
	
	public SqlWriter15(SqlWriter.Traits tr, SqlStream output, String prefix) {
		super(tr, output, prefix);
	}
	
	public void writeEndWiki() throws IOException {
		flushInsertBuffers();
		super.writeEndWiki();
	}
	
	public void writeStartPage(Page page) {
		currentPage = page;
		lastRevision = null;
	}
	
	public void writeEndPage() throws IOException {
		if (lastRevision != null) {
			updatePage(currentPage, lastRevision);
		}
		currentPage = null;
		lastRevision = null;
	}
	
	static final int DELETED_TEXT = 1;
	static final int DELETED_COMMENT = 2;
	static final int DELETED_USER = 4;
	static final int DELETED_RESTRICTED = 8;
	
	public void writeRevision(Revision revision) throws IOException {
		//TODO Canan Burayı Açmayı unutma

        bufferInsertRow(traits.getTextTable(), new Object[][] {
				{"old_id", new Integer(revision.Id)},
				{"old_text", revision.Text == null ? "" : revision.Text},
				{"old_flags", "utf-8"}});

		 //System.out.println(revision.Text);
		int rev_deleted = 0; 
		if (revision.Contributor.Username==null) rev_deleted |= DELETED_USER;
		if (revision.Comment==null) rev_deleted |= DELETED_COMMENT;
		if (revision.Text==null) rev_deleted |= DELETED_TEXT;
         //TODO CANAN Burayı Açmayı unutma

		bufferInsertRow("revision", new Object[][] {
				{"rev_id", new Integer(revision.Id)},
				{"rev_page", new Integer(currentPage.Id)},
				{"rev_text_id", new Integer(revision.Id)},
				{"rev_comment", revision.Comment == null ? "" : revision.Comment},
				{"rev_user", revision.Contributor.Username == null ? ZERO :  new Integer(revision.Contributor.Id)},
				{"rev_user_text", revision.Contributor.Username == null ? "" : revision.Contributor.Username},
				{"rev_timestamp", timestampFormat(revision.Timestamp)},
				{"rev_minor_edit", revision.Minor ? ONE : ZERO},
				{"rev_deleted", rev_deleted==0 ? ZERO : new Integer(rev_deleted) }});

        //  Canan infobox tablosuna yazdırıyor
        if (revision.InfoBox!=null)
        {

            Object[][] infoParam= new Object[revision.InfoBox.Propetys.size()+3][1];
            infoParam[0]= new Object[]{"infobox",revision.InfoBox.Text};
            infoParam[1]= new Object[]{"rev_id", new Integer(revision.Id)};
            infoParam[2]= new Object[]{"infobox_template",revision.InfoBox.Template};
            int i =3;
            Set entries = revision.InfoBox.Propetys.entrySet();
            Iterator entriesIterator = entries.iterator();
            while(entriesIterator.hasNext()){

                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                infoParam[i]= new Object[]{mapping.getKey().toString(), ""+mapping.getValue()};
                i++;
            }

            bufferInsertRow("infobox",infoParam);
        }
        //Templateler yazılıyor:
        /*
        if (revision.Patterns!=null && revision.Patterns.size()>0)
        {
            Object[][] patternParam= new Object[revision.Patterns.size()+1][1];
            patternParam[0]= new Object[]{"rev_id", new Integer(revision.Id)};
            int i =1;
            Set entries = revision.Patterns.entrySet();
            Iterator entriesIterator = entries.iterator();
            while(entriesIterator.hasNext()){

                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                patternParam[i]= new Object[]{mapping.getKey().toString(), ""+mapping.getValue()};
                i++;
            }

            bufferInsertRow("template",patternParam);
        }
        */
        //TODO Canan burası Deneme Silinecek
       // checkpoint();
		lastRevision = revision;
	}

    //Templateler yazılıyor:
    public void writeTemplate(Template template) throws IOException {

        if (template!=null)
        {
            Object[][] tempParam= new Object[template.Propetys.size()+1][1];
            tempParam[0]= new Object[]{"rev_id", new Integer(template.revisionId)};
            int i =1;
            Set entries = template.Propetys.entrySet();
            Iterator entriesIterator = entries.iterator();
            while(entriesIterator.hasNext()){

                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                tempParam[i]= new Object[]{mapping.getKey().toString(), ""+mapping.getValue()};
                i++;
            }

            bufferInsertRow("template",tempParam);
        }

        //TODO Canan burası Deneme Silinecek
         checkpoint();

    }
    //Infobox extract update
    public void updateInfoboxAnalyse(Infobox infobox) throws IOException {

        if (infobox!=null)
        {
            Object[][] infoParam= new Object[infobox.Propetys.size()+1][1];
            infoParam[0]= new Object[]{"rev_id", new Integer(infobox.rev_id)};
            int i =1;
            Set entries = infobox.Propetys.entrySet();
            Iterator entriesIterator = entries.iterator();
            while(entriesIterator.hasNext()){

                Map.Entry mapping = (Map.Entry) entriesIterator.next();
                infoParam[i]= new Object[]{mapping.getKey().toString(), ""+mapping.getValue()};
                i++;
            }

            bufferInsertRow("infobox_analyse",infoParam);
        }

        checkpoint();

    }

    public static Object[][] getArrayFromHash(HashMap<Object,Object> data){
        Object[][] arr = new Object[data.size()][2];
        Set entries = data.entrySet();
        Iterator entriesIterator = entries.iterator();

        int i = 0;
        while(entriesIterator.hasNext()){

            Map.Entry mapping = (Map.Entry) entriesIterator.next();

            arr[i][0] = mapping.getKey().toString();
            arr[i][1] = mapping.getValue().toString();

            i++;
        }
        return arr;
    }
	private static int lengthUtf8(String s) {
		final int slen = s.length();
		final char[] buf = Buffer.get(slen);
		s.getChars(0, slen, buf, 0);
		int len = 0;
		for (int i = 0; i < slen; i++) {
			char c = buf[i];
			if (c < 0x80)
				len++;
			else if (c < 0x800)
				len+=2;
			else if (c < 0xD800 || c >= 0xE000)
				len+=3;
			else {
				// Surrogate pairs are assumed to be valid.
				len+=4;
				i++;
			}
		}
		return len;
	}
	
	private void updatePage(Page page, Revision revision) throws IOException {
        //TODO CANAN Burayı Açmayı unutma

			bufferInsertRow("page", new Object[][] {
				{"page_id", new Integer(page.Id)},
				{"page_namespace", page.Title.Namespace},
				{"page_title", titleFormat(page.Title.Text)},
				{"page_restrictions", page.Restrictions},
				{"page_counter", ZERO},
				{"page_is_redirect", revision.isRedirect() ? ONE : ZERO},
				{"page_is_new", ZERO},
				{"page_random", traits.getRandom()},
				{"page_touched", traits.getCurrentTime()},
				{"page_latest", new Integer(revision.Id)},
				{"page_len", new Integer(lengthUtf8(revision.Text))}});

		checkpoint();
	}

}
