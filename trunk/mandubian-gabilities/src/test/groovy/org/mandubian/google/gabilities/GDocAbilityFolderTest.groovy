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

import java.util.ResourceBundle;

import groovy.util.GroovyTestCase
import com.google.gdata.client.docs.DocsService
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.docs.DocumentListEntry
import com.google.gdata.data.docs.FolderEntry
import org.mandubian.google.gabilities.GApiStatus
import org.mandubian.google.gabilities.GDocAbility

class GDocAbilityFolderTest extends GroovyTestCase {
	final static ResourceBundle props = ResourceBundle.getBundle("gdata")
	
	static DocsService service = new DocsService(props.getString("applicationname"))
	
	final static String username = props.getString("username")
	final static String password = props.getString("password")
	
	final static String folderName = props.getString("foldername")


	static {
		service.setUserCredentials(username, password)
	}

	void setUp() {
	}

	void testGetFolderNoFolderException() {
        use(GDocAbility) {
        	def (folder, res) = service.getFolder(folderName)
        	assertTrue(res == GApiStatus.NOT_FOUND)
        	assertNull(folder)
        }
    }
	
    void testCreateFolder() {
        use(GDocAbility) {
        	def (folder, res) = service.createFolder(folderName)
        	
        	println("waitForFolderCreation:"+service.waitForFolderCreation(folderName))
        }
    }
    
   
    void testCreateFolderAlreadyExistsException() {
        use(GDocAbility) {
        	def (folder, res) = service.createFolder(folderName)
        	assertTrue(res == GApiStatus.ALREADY_EXISTS)
        	assertTrue(folder == null)
        }
    }

    
    
    void testGetFolder() {
        use(GDocAbility) {
        	def (folder, res) = service.getFolder(folderName)
        	assertTrue(res == GApiStatus.SINGLE_FOUND)
        	assertNotNull(folder)
        	assertEquals(folder.getTitle().getPlainText(), folderName)
        }
    }
    
    void testDeleteFolder() {
        use(GDocAbility) {
        	def(res,nb) = service.deleteFolder(folderName)
        	assertTrue(res == GApiStatus.SINGLE_DELETED)
        	assertTrue(nb == 1)

        	println("waitForFolderDeletion:"+service.waitForFolderDeletion(folderName))
	    }
    }

    
    void testCreateFolderAgain() {
        use(GDocAbility) {
        	def (folder, res) = service.createFolder(folderName)
   	
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(folder)
        	assertEquals(folder.getTitle().getPlainText(), folderName)

        	println("waitForFolderCreation:"+service.waitForFolderCreation(folderName))
        }
    }    
        
    void testCreateFolderForce() {
        use(GDocAbility) {
        	def (folder, res) = service.createFolder(
        			folderName, 
        			force: true) 	

        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(folder)
        	assertEquals(folder.getTitle().getPlainText(), folderName)

        	println("waitForFolderCreation:"+service.waitForFolderCreation(folderName, multiple: true))
        }
    }
    
	void testGetFolderMultipleFoldersException() {
        use(GDocAbility) {
        	def (folders, res) = service.getFolder(folderName)
        	
        	assertTrue(res == GApiStatus.MULTIPLE_FOUND)
        	assertNotNull(folders)
        	assertTrue(folders.size() > 1)
        }
    }

    void testDeleteFolderMultipleFoldersException() {
        use(GDocAbility) {
	        def (res,nb) = service.deleteFolder(folderName)

	        assertTrue(res == GApiStatus.MULTIPLE_FOUND)	
	        assertTrue(nb != 0)
        }
    }

    void testDeleteFolderMultipleFoldersForce() {
    	use(GDocAbility) {
	        def (res,nb) = service.deleteFolder(folderName, multiple : true)

	        assertTrue(res == GApiStatus.MULTIPLE_DELETED)
	        assertTrue(nb > 0)
        	println("waitForFolderDeletion:"+service.waitForFolderDeletion(folderName))
        }
    }

    void testDeleteFolderNoFolderException() {
        use(GDocAbility) {
       		def (res,nb) = service.deleteFolder(folderName)
       		assertTrue(res == GApiStatus.NOT_FOUND)
       		assertTrue(nb == 0)
        }
    }

    void testGetFolderTrashed() {
        use(GDocAbility) {
       		def (folders, res) = service.getFolder(folderName, trashed:true)
       		assertTrue(folders.size() > 0)
        }
    }
    
    void tearDown() {
    }
}