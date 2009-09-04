/*
 * Copyright © 2009 mandubian. All Rights Reserved.

 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products 
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY MANDUBIAN "AS IS" AND ANY EXPRESS OR IMPLIED 
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.mandubian.google.gabilities;

import groovy.util.GroovyTestCase
import com.google.gdata.client.docs.DocsService
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.docs.DocumentListEntry
import com.google.gdata.data.docs.FolderEntry
import org.mandubian.google.gabilities.GApiStatus
import org.mandubian.google.gabilities.GDocAbility

class GDocSpreadsheetAbilitySingleTest extends GroovyTestCase {
	final static ResourceBundle props = ResourceBundle.getBundle("gdata")
		
	static DocsService docService = new DocsService(props.getString("applicationname"))
	static SpreadsheetService spreadService = new SpreadsheetService(props.getString("applicationname"))
	
	final static String username = props.getString("username")
	final static String password = props.getString("password")
	final static String csvfilepath = props.getString("csvfilepath")
	
	final static String folderName = props.getString("foldername")
	final static String folderDocName = props.getString("folderdocname")
	final static String docName = props.getString("docname")
	final static String worksheetName = props.getString("worksheetname")

	static {
		docService.setUserCredentials(username, password)
		spreadService.setUserCredentials(username, password)
	}

	void setUp() {
	}
	


	void testCreateSpreadsheetFile() {
        use(GDocAbility, GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName, new File(csvfilepath))    	
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        	
        	println("waitForDocumentCreation:"
        			+docService.waitForDocumentCreation(docName))
        }
    }	
	
	
    void tearDown() {
    }
}