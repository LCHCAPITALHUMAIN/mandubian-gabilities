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

class GDocSpreadsheetAbilityTest extends GroovyTestCase {
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
	

//there is something strange in Spreadsheet API: when you delete a document
//using DOCS API, it stays in the Spreadsheet feed
//	void testGetSpreadsheetNOT_FOUND() {
//        use(GSpreadsheetAbility) {
//        	def (spreadsheet,res) = 
//        		spreadService.getSpreadsheet(docName)
//        	spreadsheet.each { println(it.getTitle().getPlainText()) }
//        	assertTrue(res == GApiStatus.NOT_FOUND)
//        	assertNull(spreadsheet)
//        }
//    }


	void testGetSpreadsheetDocNOT_FOUND() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getSpreadsheet(docName)    	
         	assertTrue(res == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)
        }
    }	

	void testCreateSpreadsheetOK() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName)    	
        	        
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        	
        	println("waitForSpreadsheetCreation:"
        			+docService.waitForSpreadsheetCreation(docName))

        }
    }
	
	void testGetSpreadsheetSINGLE_FOUND() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getSpreadsheet(docName)    	

        	assertTrue(res == GApiStatus.SINGLE_FOUND)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        }
    }


	void testConvertSpreadsheetFromDocAndConvert() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet,res) = 
        		docService.getSpreadsheet(docName)
        		
        	assertTrue(res == GApiStatus.SINGLE_FOUND)

        	def spreadsheet = 
        		spreadService.convertSpreadsheetFromDoc(docSpreadsheet)    	
        	        
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)

        }
    }
	
	void testCreateSpreadsheetALREADY_EXISTS() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName)    	
        	assertTrue(res == GApiStatus.ALREADY_EXISTS)
        	assertNull(spreadsheet)        	   
        }
    }


	void testDeleteSpreadsheet() {
        use(GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			docName)       	
       	
        	assertTrue(res == GApiStatus.SINGLE_DELETED)		
        	assertTrue(nb == 1)		

        	println("waitForSpreadsheetDeletion:"
        			+docService.waitForSpreadsheetDeletion(docName))
        	
        	def (spreadsheet, res2) = 
        		docService.getSpreadsheet(
            			docName)
        	
            assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)        	
        }
    } 

	void testCreateSpreadsheetAgain() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName)    	
        	        
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        	
        	println("waitForSpreadsheetCreation:"
        			+docService.waitForSpreadsheetCreation(docName))
        }
    }

	void testCreateSpreadsheetFORCE() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName,
        				force:true)    	
        	        
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        	
        	println("waitForSpreadsheetCreation:"
        			+docService.waitForSpreadsheetCreation(docName, multiple:true))
        }
    }
	
	void testGetSpreadsheetNOT_EXACT() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getSpreadsheet(docName,
        				exact: false)    	
        	        	
        	assertTrue(res == GApiStatus.MULTIPLE_FOUND)
        	assertTrue(spreadsheet in List)
        	assertTrue(spreadsheet.size()>1)
        }
    }	
	
	void testGetSpreadsheetNOT_EXACT2() {
        use(GDocAbility, GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getDocument(docName.substring(0,5),
        				exact: false)    	
        	assertTrue(res == GApiStatus.MULTIPLE_FOUND)
        	
        	assertTrue(spreadsheet in List)
        	assertTrue(spreadsheet.size()>1)
        }
    }	
	
	
	void testGetSpreadsheetMULTIPLE_FOUND() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getSpreadsheet(docName)    	
        	        	
        	assertTrue(res == GApiStatus.MULTIPLE_FOUND)
        	assertTrue(spreadsheet in List)
        	assertTrue(spreadsheet.size()>1)
        }
    }


	void testDeleteSpreadsheetMULTIPLE() {
        use(GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			docName,
        			multiple: true)       	
     	
        	assertTrue(res == GApiStatus.MULTIPLE_DELETED)		
        	assertTrue(nb == 2)		

        	println("waitForSpreadsheetDeletion:"
        			+docService.waitForSpreadsheetDeletion(docName,
                			multiple: true))
        	
        	def (spreadsheet, res2) = 
        		docService.getSpreadsheet(docName)
       		assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)
        	
        }
    }

	void testCreateSpreadsheetAgainAgain() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(docName)    	
       	        
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), docName)
        	
        	println("waitForSpreadsheetCreation:"
        			+docService.waitForSpreadsheetCreation(docName))
        }
    }
	

//PROBLEM with spreadservice feed which keeps all entries even if
//they have been deleted from DOCS API
// 	
//	void testGetWorksheet() {
//        use(GSpreadsheetAbility) {
//        	def (worksheet,res) = spreadService.getWorksheet(
//        			docName, "Sheet 1")    	
//        			println("res:"+res)
//        	assertTrue(res == GApiStatus.SINGLE_FOUND)		
//        	assertNotNull(worksheet)
//        	assertEquals(worksheet.getTitle().getPlainText(), "Sheet 1")
//        }
//    }

	
	void testGetWorksheetFromSpreadsheet() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet, res) = 
        		docService.getSpreadsheet(docName)
        		
        	def spreadsheet = 
        		spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
        		
        	assertTrue(res == GApiStatus.SINGLE_FOUND)
        	assertNotNull(spreadsheet)
        	
        	def (worksheet, res2) = spreadsheet.getWorksheet("Sheet 1")    	
        	        	
        	assertTrue(res == GApiStatus.SINGLE_FOUND)		
        	assertNotNull(worksheet)
        	assertEquals(worksheet.getTitle().getPlainText(), "Sheet 1")
        }
    }
	

	void testAddWorksheet() {
        use(GSpreadsheetAbility) {
			def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (worksheet, res2) = spreadsheet.addWorksheet(worksheetName)
        			
        	assertTrue(res2 == GApiStatus.OK)		
        	assertNotNull(worksheet)
        	assertEquals(worksheet.getTitle().getPlainText(), worksheetName)
        }
    }

	
	void testDeleteWorksheet() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (worksheet, res2) = spreadsheet.getWorksheet(worksheetName) 
        	worksheet.delete()    			
        			
        	(worksheet, res2) = spreadsheet.getWorksheet(worksheetName)    	
        	
        	assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(worksheet)
        	
        }
    }
	
	void testAddWorksheetAgain() {
        use(GSpreadsheetAbility) {
			def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (worksheet, res2) = spreadsheet.addWorksheet(worksheetName)
        			
        	assertTrue(res2 == GApiStatus.OK)		
        	assertNotNull(worksheet)
        	assertEquals(worksheet.getTitle().getPlainText(), worksheetName)
        }
    }

	void testAddWorksheetAlreadyExists() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (worksheet, res2) = spreadsheet.addWorksheet(worksheetName)
	    	assertTrue(res2 == GApiStatus.ALREADY_EXISTS)
	    	assertNull(worksheet)  
        }
    }
	
	void testAddWorksheetForce() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (worksheet, res2) = spreadsheet.addWorksheet(
        			worksheetName,
        			force:true)
        			
        	assertTrue(res2 == GApiStatus.OK)		
        	assertNotNull(worksheet)
        	assertEquals(worksheet.getTitle().getPlainText(), worksheetName)
        }
    }

	void testDeleteWorksheetMULTIPLE() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet, res) = 
				docService.getSpreadsheet(docName)
	
			def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
				
        	def (res2, nb) =
        		spreadsheet.deleteWorksheet(worksheetName, multiple:true)
        	assertTrue(res2 == GApiStatus.MULTIPLE_DELETED)
        	assertTrue(nb == 2)
        	
        	def (worksheet, res3) = 
        		spreadsheet.getWorksheet(worksheetName)
       		assertTrue(res3 == GApiStatus.NOT_FOUND)
        	assertNull(worksheet)
        	
        }
    }
	
	void testGetSpreadsheetDocFolderNOT_FOUND() {
	    use(GSpreadsheetAbility, GDocAbility) {
	    	def (folder, res) = docService.createFolder(folderName)
	    	assertTrue(res == GApiStatus.OK)
	    	
	    	println("waitForFolderCreation:"
        			+docService.waitForFolderCreation(folderName))

	    	def (spreadsheet,res2) = 
	    		docService.getSpreadsheet(
	    				folderDocName,
	    				folderName)    	
	    	        	
	    	assertTrue(res2 == GApiStatus.NOT_FOUND)
	    	assertNull(spreadsheet)
	    }
	}	

	void testCreateSpreadsheetFolderOK() {
	    use(GDocAbility, GSpreadsheetAbility) {
	    	def (spreadsheet,res) = 
	    		docService.createSpreadsheet(
	    				folderDocName,
	    				folderName)    	
	    	        
	    	assertTrue(res == GApiStatus.OK)
	    	assertNotNull(spreadsheet)
	    	assertEquals(spreadsheet.getTitle().getPlainText(), folderDocName)
	    	
	    	println("waitForDocumentCreation:"
        			+docService.waitForDocumentCreation(folderDocName, folderName))
	    }
	}	

	void testGetSpreadsheetFolderSINGLE_FOUND() {
	    use(GSpreadsheetAbility) {
	    	def (spreadsheet,res) = 
	    		docService.getSpreadsheet(
	    				folderDocName,
	    				folderName)    	
	    	        	
	    	assertTrue(res == GApiStatus.SINGLE_FOUND)
	    	assertNotNull(spreadsheet)
	    	assertEquals(spreadsheet.getTitle().getPlainText(), folderDocName)
	    }
	}

	void testDeleteSpreadsheetFolder() {
        use(GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			folderDocName,
    				folderName)       	
       	
        	assertTrue(res == GApiStatus.SINGLE_DELETED)		
        	assertTrue(nb == 1)		

        	println("waitForSpreadsheetDeletion:"
        			+docService.waitForSpreadsheetDeletion(
        					folderDocName,
            				folderName))
        	
        	def (spreadsheet, res2) = 
        		docService.getSpreadsheet(
        				folderDocName,
        				folderName)
        	
            assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)        	
        }
    } 
	
	void testCreateSpreadsheetFolderAgain() {
	    use(GDocAbility, GSpreadsheetAbility) {
	    	def (spreadsheet,res) = 
	    		docService.createSpreadsheet(
	    				folderDocName,
	    				folderName)    	
	    	        
	    	assertTrue(res == GApiStatus.OK)
	    	assertNotNull(spreadsheet)
	    	assertEquals(spreadsheet.getTitle().getPlainText(), folderDocName)
	    	
	    	println("waitForDocumentCreation:"
        			+docService.waitForDocumentCreation(folderDocName, folderName))
	    }
	}	
	
	void testCreateSpreadsheetFolderALREADY_EXISTS() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(
        				folderDocName,
        				folderName)    	
        	        
        	assertTrue(res == GApiStatus.ALREADY_EXISTS)
        	assertNull(spreadsheet)        	   
        }
    }
	
	void testCreateSpreadsheetFolderFORCE() {
        use(GDocAbility, GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.createSpreadsheet(
        				folderDocName,
        				folderName,
        				force:true)    	
        	        
        	assertTrue(res == GApiStatus.OK)
        	assertNotNull(spreadsheet)
        	assertEquals(spreadsheet.getTitle().getPlainText(), folderDocName)
        	
        	println("waitForDocumentCreation:"
        			+docService.waitForDocumentCreation(folderDocName, folderName, multiple:true))
        }
    }

	
	void testGetSpreadsheetFolderMULTIPLE_FOUND() {
        use(GSpreadsheetAbility) {
        	def (spreadsheet,res) = 
        		docService.getSpreadsheet(
        				folderDocName,
        				folderName)    	
        	        	
        	assertTrue(res == GApiStatus.MULTIPLE_FOUND)
        	assertTrue(spreadsheet in List)
        	assertTrue(spreadsheet.size()>1)
        }
    }

	void testDeleteSpreadsheetFolderMULTIPLE() {
        use(GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			folderDocName,
        			folderName,
        			multiple: true)       	
     	
        	assertTrue(res == GApiStatus.MULTIPLE_DELETED)		
        	assertTrue(nb == 2)		

        	println("waitForSpreadsheetDeletion:"
        			+docService.waitForSpreadsheetDeletion(
        					folderDocName, folderName,
                			multiple: true))
        	
        	def (spreadsheet, res2) = 
        		docService.getSpreadsheet(
        				folderDocName,
            			folderName)
       		assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)
        	
        }
    }

	void testDeleteSpreadsheetFinal() {
        use(GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			docName)       	
       	
        	assertTrue(res == GApiStatus.SINGLE_DELETED)		
        	assertTrue(nb == 1)		

        	println("waitForSpreadsheetDeletion:"
        			+docService.waitForSpreadsheetDeletion(
        					docName))
        	
        	def (spreadsheet, res2) = 
        		docService.getSpreadsheet(
        				docName)
        	
            assertTrue(res2 == GApiStatus.NOT_FOUND)
        	assertNull(spreadsheet)        	
        }
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
	
	void testGetSpreadsheetByKey() {
        use(GSpreadsheetAbility) {
        	def (docSpreadsheet,res) = 
        		docService.getSpreadsheet(docName)
        		
        	def spreadsheet = 
				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
        		
        	def (spreadsheet2,res2) = 
	    		spreadService.getSpreadsheetByKey(spreadsheet.getKey())   	  
        	assertTrue(res2 == GApiStatus.OK)
        	assertNotNull(spreadsheet2)
        }
    }	

// DOESN'T WORK DUE TO SPREADSHEET/DOCS DELETION ISSUE
// 
//	void testDeleteSpreadsheetByKey() {
//        use(GSpreadsheetAbility) {
//			def (docSpreadsheet,res) = 
//				docService.getSpreadsheet(docName)
//				
//			def spreadsheet = 
//				spreadService.convertSpreadsheetFromDoc(docSpreadsheet)
//				
//			def (spreadsheet2,res2) = 
//				spreadService.getSpreadsheetByKey(spreadsheet.getKey())   
//			assertTrue(res2 == GApiStatus.OK)
//        	assertNotNull(spreadsheet2)
//        	
//        	spreadsheet2.delete()
//        }
//    }
	
	void testDeleteSpreadsheetMULTIPLEFinal() {
        use(GDocAbility, GSpreadsheetAbility) {
        	def (res,nb) = docService.deleteSpreadsheet(
        			docName,
        			multiple: true)       	
        	
        	(res,nb) = docService.deleteFolder(folderName)       	
        	        	        	
        }
    }	
	
    void tearDown() {
    }
}