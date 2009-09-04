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
package org.mandubian.google.gabilities

import java.util.List;

import com.google.gdata.util.AuthenticationException
import com.google.gdata.util.ServiceException
import com.google.gdata.client.docs.DocsService
import com.google.gdata.data.PlainTextConstruct
import com.google.gdata.data.docs.DocumentListEntry
import com.google.gdata.data.docs.DocumentListFeed
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.docs.SpreadsheetEntry
import com.google.gdata.client.DocumentQuery

import org.mandubian.google.gabilities.GApiConstants
import org.mandubian.google.gabilities.GApiStatus

/**
 * @author mandubian
 */
public class GDocAbility{	
	
	/**
	 * retrieves one or several Folder(s) as FolderEntry querying DocumentFeed 
	 * with a name (exact match or not) and manages single/multiple results using 
	 * tuple results (FolderEntry(s), GApiStatus).
	 * <i>Take into account the result is a tuple and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (folder, res) = service.getFolder("test1")<br/>
	 * def (folder) = service.getFolder("test1")<br/>
	 * def (_,res) = service.getFolder("test1")<br/>
	 * def (folder,res) = service.getFolder("test1", exact:false)<br/>
	 * def (folder,res) = service.getFolder("test1", hidden:true, exact:false)<br/>
	 * def (folder,res) = service.getFolder("test1",  exact:false, trashed:true, hidden:false,)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options.<br/>
	 * 						For example:<br/>
	 * 						exact: true
	 * 						hidden: true
	 * 						trashed: true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 												This is the same as google api "title-exact" query parameter.
	 * 												</td><td>boolean</td><td>true</td></tr>
	 * 							<tr><th>hidden</th><td>specifies whether the hidden docs should be shown.
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>trashed</th><td>specifies whether the trashed docs should be shown 
	 * 													(apparently, trashed docs are not returned for the time being so this options is not useful now).
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 							<li>(FolderEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							<li>(List&lt;FolderEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 * 
	 */
	public static getFolder(
			final DocsService self, 
			final options=[:], 
			final String foldername) 
				throws 	MalformedURLException, IOException, ServiceException {
		def exact = (options["exact"]?options["exact"]:true)
		def hidden = (options["hidden"]?options["hidden"]:false)
		def trashed = (options["trashed"]?options["trashed"]:false)

		def feed = 
			self.getFeed(
					new URL("${GApiConstants.feedBaseUrlDoc}/-/folder?title=${foldername}&title-exact=${exact}"), 
					DocumentListFeed.class)
		
		def l = []      
		feed.getEntries()?.each {
			def h = it.isHidden()
			def t = it.isTrashed()
			if(h && t) { 
				if(hidden) l << it
				else if(trashed) l << it
			}
			else if(h) {
				if(hidden) l << it
			}
			else if(t){
				if(trashed) l << it
			}
			else l << it
		}
		switch(l.size())
		{
		case 0:
			return [ null, GApiStatus.NOT_FOUND ]
		case 1:
			return [ l.get(0), GApiStatus.SINGLE_FOUND ]			
		default:
			return [ l, GApiStatus.MULTIPLE_FOUND ]
		}
	}

	/**
	 * creates a new Folder.
	 * <i>Take into account the result is a tuple (FolderEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (folder, res) = service.createFolder("test1")<br/>
	 * def (folder) = service.createFolder("test1")<br/>
	 * def (_, res) = service.createFolder("test1")<br/>
	 * def (folder, res) = service.createFolder("test1", force: true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						force: true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>force</th><td>forces or not the creation when an existing folder with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(FolderEntry, OK)</li>
	 * 							<li>(null, ALREADY_EXISTS)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static createFolder(
			final DocsService self, 
			final options=[:], 
			final String foldername)
				throws 	MalformedURLException, IOException, ServiceException {
		def force = (options["force"]?options["force"]:false)
		
		def (folder, res) = self.getFolder(foldername)
		switch(res)
		{
		case GApiStatus.NOT_FOUND:
			FolderEntry newEntry = new FolderEntry()
			newEntry.setTitle(new PlainTextConstruct(foldername))

			return [ self.insert(new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
			         GApiStatus.OK ]
		default:
			if(force) {
				FolderEntry newEntry = new FolderEntry()
				newEntry.setTitle(new PlainTextConstruct(foldername))

				return [ self.insert(new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
				         GApiStatus.OK ]
			}
			return [ null, GApiStatus.ALREADY_EXISTS]
		}

	}

	/**
	 * deletes one or several Folder(s).
	 * <i>Take into account the result is a tuple (GApiStatus, nbDeletedorFoundEntries) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb) = service.deleteFolder("test1")<br/>
	 * def (res) = service.deleteFolder("test1", multiple:true)<br/>
	 * def (res, _) = service.deleteFolder("test1", permanent:true)<br/>
	 * def (res, nb) = service.deleteFolder("test1", multiple:true, permanent:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param foldername	the folder name to delete.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						multiple:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>permanent</th><td>Allows deleting permanently (not trashed) found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(NOT_FOUND, 0)</li>
	 * 							<li>(SINGLE_DELETED, 1)</li>
	 * 							<li>(MULTIPLE_DELETED, nb_delete)</li>
	 * 							<li>(MULTIPLE_FOUND, nb_found)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static deleteFolder(
			final DocsService self,
			final options = [:],
			final String foldername)
				throws 	MalformedURLException, IOException, ServiceException {
		def multiple = (options["multiple"]?options["multiple"]:false)
		def permanent = (options["permanent"]?options["permanent"]:false)
		
        def (folder, res) = self.getFolder(foldername)
		switch(res)
		{
		case GApiStatus.NOT_FOUND: return [ GApiStatus.NOT_FOUND, 0 ]
		case GApiStatus.SINGLE_FOUND: 
			self.delete(new URL(folder.getEditLink().getHref() + "?delete=${permanent}"), folder.getEtag())
			return [ GApiStatus.SINGLE_DELETED, 1 ]
		case GApiStatus.MULTIPLE_FOUND:
			if(multiple) {
				folder.each { 
					self.delete(new URL(it.getEditLink().getHref() + "?delete=${permanent}"), it.getEtag())
				}
				return [ GApiStatus.MULTIPLE_DELETED, folder.size() ] 
			}
			return [ GApiStatus.MULTIPLE_FOUND, folder.size() ]
		}
	}
		
	/**
	 * retrieves one or several Document(s) as DocumentListEntry querying DocumentFeed 
	 * with a name (exact match or not) and manages single/multiple results 
	 * using tuple results (DocumentListEntry(s), GApiStatus).
	 * <i>Take into account the result is a tuple and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (doc, res) = service.getDocument("test1")<br/>
	 * def (doc) = service.getDocument("test1")<br/>
	 * def (_,res) = service.getDocument("test1")<br/>
	 * def (folder,res) = service.getDocument("test1", exact:false)<br/>
	 * def (folder,res) = service.getDocument("test1", hidden:true, exact:false)<br/>
	 * def (folder,res) = service.getDocument("test1",  exact:false, trashed:true, hidden:false,)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						[ "exact": true ]
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 												This is the same as google api "title-exact" query parameter.
	 * 												</td><td>boolean</td><td>true</td></tr>
	 * 							<tr><th>hiddden</th><td>specifies whether the hidden docs should be shown.
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>trashed</th><td>specifies whether the trashed docs should be shown 
	 * 													(apparently, trashed docs are not returned for the time being so this options is not useful now).
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 							<li>(DocumentListEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							<li>(List&lt;DocumentListEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 * 
	 */
	public static getDocument(
			final DocsService self,
			final options = [:],
			final String docname)
				throws 	MalformedURLException, IOException, ServiceException {
		def exact = options["exact"]!=null?options["exact"]:true
		def hidden = options["hidden"]!=null?options["hidden"]:false
		def trashed = options["trashed"]!=null?options["trashed"]:false
		DocumentListFeed feed = 
			self.getFeed(
					new URL("${GApiConstants.feedBaseUrlDoc}?title=${docname}&title-exact=${exact}"), 
					DocumentListFeed.class)

		def l = []      
		feed.getEntries()?.each {
			def h = it.isHidden()
			def t = it.isTrashed()
			if(h && t) { 
				if(hidden) l << it
				else if(trashed) l << it
			}
			else if(h) {
				if(hidden) l << it
			}
			else if(t){
				if(trashed) l << it
			}
			else l << it
		}
		switch(l.size())
		{
		case 0:
			return [ null, GApiStatus.NOT_FOUND ]
		case 1:
			return [ l.get(0), GApiStatus.SINGLE_FOUND ]			
		default:
			return [ l, GApiStatus.MULTIPLE_FOUND ]
		}				
	}


	/**
	 * retrieves one or several Document(s) in a folder as DocumentListEntry 
	 * querying DocumentFeed with a name (exact match or not) and manages 
	 * single/multiple results using tuple results (DocumentListEntry(s), GApiStatus).
	 * <i>Take into account the result is a tuple and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (doc, res) = service.getDocument("test1", "folder1")<br/>
	 * def (doc) = service.getDocument("test1", "folder1")<br/>
	 * def (_,res) = service.getDocument("test1", "folder1")<br/>
	 * def (folder,res) = service.getDocument("test1", "folder1", exact:false)<br/>
	 * def (folder,res) = service.getDocument("test1", "folder1", hidden:true, exact:false)<br/>
	 * def (folder,res) = service.getDocument("test1", "folder1", exact:false, trashed:true, hidden:false,)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to look for.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						[ "exact": true ]
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 												This is the same as google api "title-exact" query parameter.
	 * 												</td><td>boolean</td><td>true</td></tr>
	 * 							<tr><th>hiddden</th><td>specifies whether the hidden docs should be shown.
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>trashed</th><td>specifies whether the trashed docs should be shown 
	 * 													(apparently, trashed docs are not returned for the time being so this options is not useful now).
	 * 												</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 							<li>(DocumentListEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							<li>(List&lt;DocumentListEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 * 
	 */
	public static getDocument(
			final DocsService self, 
			final options= [:],
			final String docname, 
			final String foldername)
				throws 	MalformedURLException, IOException, ServiceException {
		def exact = (options["exact"]?options["exact"]:true)
		def hidden = (options["hidden"]?options["hidden"]:false)
		def trashed = (options["trashed"]?options["trashed"]:false)

		DocumentListFeed feed = 
			self.getFeed(new URL("${GApiConstants.feedBaseUrlDoc}/-/${foldername}?title=${docname}&title-exact=${exact}"), 
					DocumentListFeed.class)			
					
		def l = []      
 		feed.getEntries()?.each {
 			def h = it.isHidden()
 			def t = it.isTrashed()
 			if(h && t) { 
 				if(hidden) l << it
 				else if(trashed) l << it
 			}
 			else if(h) {
 				if(hidden) l << it
 			}
 			else if(t){
 				if(trashed) l << it
 			}
 			else l << it
 		}
 		switch(l.size())
 		{
 		case 0:
 			return [ null, GApiStatus.NOT_FOUND ]
 		case 1:
 			return [ l.get(0), GApiStatus.SINGLE_FOUND ]			
 		default:
 			return [ l, GApiStatus.MULTIPLE_FOUND ]
 		}							
	}

	/**
	 * deletes one or several Document(s).
	 * <i>Take into account the result is a tuple (GApiStatus, nbDeletedorFoundEntries) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (res,nb) = service.deleteDocument("test1")<br/>
	 * def (res) = service.deleteDocument("test1", multiple:true)<br/>
	 * def (res,_) = service.deleteDocument("test1", permanent:true)<br/>
	 * def (res,nb) = service.deleteDocument("test1", multiple:true, permanent:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to delete.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						[ "multiple": true ]
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>permanent</th><td>Allows deleting permanently (not trashed) found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(NOT_FOUND, 0)</li>
	 * 							<li>(SINGLE_DELETED, 1)</li>
	 * 							<li>(MULTIPLE_DELETED, nb_delete)</li>
	 * 							<li>(MULTIPLE_FOUND, nb_delete)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static deleteDocument(
			final DocsService self,
			final options= [:],
			final String docname)
				throws 	MalformedURLException, IOException, ServiceException {
		def multiple = (options["multiple"]?options["multiple"]:false)
		def permanent = (options["permanent"]?options["permanent"]:false)

		def (doc, res) = self.getDocument(docname)
		
		switch(res){
		case GApiStatus.NOT_FOUND:
			return [ GApiStatus.NOT_FOUND, 0 ]
			
		case GApiStatus.SINGLE_FOUND:
			self.delete(new URL(doc.getEditLink().getHref() + "?delete=${permanent}"), doc.getEtag())
			
			return [ GApiStatus.SINGLE_DELETED, 1 ]
			
		case GApiStatus.MULTIPLE_FOUND:
			if(multiple) {
				doc.each { 
					self.delete(new URL(it.getEditLink().getHref() + "?delete=${permanent}"), it.getEtag())
				}
				return [ GApiStatus.MULTIPLE_DELETED, doc.size() ]
			}
			else return [ GApiStatus.MULTIPLE_FOUND, doc.size() ]
			
		default:
			return [ GApiStatus.BAD_STATE, 0 ]
		}
	}

	/**
	 * deletes one or several Document(s) in a folder.
	 * <i>Take into account the result is a tuple (GApiStatus, nbDeletedorFoundEntries) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (res,nb) = service.deleteDocument("test1", "folder1")<br/>
	 * def (res) = service.deleteDocument("test1", "folder1", multiple:true)<br/>
	 * def (res, _) = service.deleteDocument("test1", "folder1", permanent:true)<br/>
	 * def (res, nb) = service.deleteDocument("test1", "folder1", multiple:true, permanent:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to delete.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						multiple:true
	 * 						permanent:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 							<tr><th>permanent</th><td>Allows deleting permanently (not trashed) found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(NOT_FOUND, 0)</li>
	 * 							<li>(SINGLE_DELETED, 1)</li>
	 * 							<li>(MULTIPLE_DELETED, nb_delete)</li>
	 * 							<li>(MULTIPLE_FOUND, nb_delete)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static deleteDocument(
			final DocsService self, 
			final options = [:],
			final String docname, 
			final String foldername)
				throws 	MalformedURLException, IOException, ServiceException {
		def multiple = (options["multiple"]?options["multiple"]:false)
		def permanent = (options["permanent"]?options["permanent"]:false)

		def (doc, res) = self.getDocument(docname, foldername)
		
		switch(res){
		case GApiStatus.NOT_FOUND:
			return [ GApiStatus.NOT_FOUND, 0 ]
			
		case GApiStatus.SINGLE_FOUND:
			self.delete(new URL(doc.getEditLink().getHref() + "?delete=${permanent}"), doc.getEtag())
			return [ GApiStatus.SINGLE_DELETED, 1 ]
			
		case GApiStatus.MULTIPLE_FOUND:
			if(multiple) {
				doc.each { 
					self.delete(new URL(it.getEditLink().getHref() + "?delete=${permanent}"), it.getEtag())
				}
				return [ GApiStatus.MULTIPLE_DELETED, doc.size() ]
			}
			else return [ GApiStatus.MULTIPLE_FOUND, doc.size() ]
			
		default:
			return [ GApiStatus.BAD_STATE, 0 ]
		}			
	}

	final static int MAX_COUNT = 10
	final static int SLEEP_DELAY = 1000
	
	/**
	 * waits for folder deletion by interrogating the DocsService periodically for this folder 
	 * until it is not found anymore and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForFolderDeletion("folder1")<br/>
	 * def (res, nb_ms) = service.waitForFolderDeletion("folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForFolderDeletion("folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForFolderDeletion("folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay": 2000
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_delete_detection)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForFolderDeletion(
				final DocsService self, 
				final options=[:],
				String folderName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)

		// horrible cludge to wait for google to index the doc 
    	def i=0
    	while(i<maxcount){
    		sleep(delay)

    		def (_, res2) = self.getFolder(folderName)    		
    		if(res2 == GApiStatus.NOT_FOUND) {
    			return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}

	/**
	 * waits for folder creation by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForFolderCreation("folder1")<br/>
	 * def (res, nb_ms) = service.waitForFolderCreation("folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForFolderCreation("folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForFolderCreation("folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay: 2000
	 * 						multiple: false
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 							<tr><th>multiple</th><td>verifies multiple folders have been created</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_detect_document)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForFolderCreation(
				final DocsService self,
				final options = [:],
				String folderName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)
		boolean multiple = (options["multiple"]?options["multiple"]:false)
            	
        def i = 0
    	while(i<maxcount){
    		sleep(delay)
    		def (_, res) = self.getFolder(folderName)    		
    		if(res != GApiStatus.NOT_FOUND) {
    			if(multiple){
    				if(res==GApiStatus.MULTIPLE_FOUND)
    					return [ GApiStatus.OK, i*delay]
    			}
				else return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}	


	/**
	 * waits for document creation by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1")<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay: 2000
	 * 						multiple: false
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 							<tr><th>multiple</th><td>verifies multiple folders have been created</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_detect_document)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForDocumentCreation(
				final DocsService self,
				final options = [:],
				String docName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)
		boolean multiple = (options["multiple"]?options["multiple"]:false)
            	
        def i = 0
    	while(i<maxcount){
    		sleep(delay)
    		def (_, res) = self.getDocument(docName)    		
    		if(res != GApiStatus.NOT_FOUND) {
    			if(multiple){
    				if(res==GApiStatus.MULTIPLE_FOUND)
    					return [ GApiStatus.OK, i*delay]
    			}
				else return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}	

	/**
	 * waits for document creation in a folder by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", "folder1")<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", "folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", "folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForDocumentCreation("doc1", "folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the document name to look for.
	 * @param foldername	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay: 2000
	 * 						multiple: false
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 							<tr><th>multiple</th><td>verifies multiple folders have been created</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_detect_document)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForDocumentCreation(
				final DocsService self,
				final options = [:],
				String docName,
				String folderName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)
		boolean multiple = (options["multiple"]?options["multiple"]:false)
            	
        def i = 0
    	while(i<maxcount){
    		sleep(delay)
    		def (_, res) = self.getDocument(docName, folderName)    		
    		if(res != GApiStatus.NOT_FOUND) {
    			if(multiple){
    				if(res==GApiStatus.MULTIPLE_FOUND)
    					return [ GApiStatus.OK, i*delay]
    			}
				else return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}
	
	/**
	 * waits for document deletion by interrogating the DocsService periodically for this folder 
	 * until it is not found anymore and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1")<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the doc name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay": 2000
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_delete_detection)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForDocumentDeletion(
				final DocsService self, 
				final options=[:],
				String docName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)

		// horrible cludge to wait for google to index the doc 
    	def i=0
    	while(i<maxcount){
    		sleep(delay)

    		def (_, res2) = self.getDocument(docName)    		
    		if(res2 == GApiStatus.NOT_FOUND) {
    			return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}	

	/**
	 * waits for document deletion in a folder by interrogating the DocsService periodically for this folder 
	 * until it is not found anymore and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", "folder1")<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", "folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", "folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForDocumentDeletion("doc1", "folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the doc name to look for.
	 * @param folderName	the doc name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						maxcount: 15
	 * 						delay": 2000
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>maxcount</th><td>maximum retry number before giving-up</td><td>integer</td><td>10</td></tr>
	 * 							<tr><th>delay</th><td>delay of sleep between 2 tries</td><td>integer</td><td>1000</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(GApiStatus.OK, number_milliseconds_to_delete_detection)</li>
	 * 							<li>(GApiStatus.KO, number_milliseconds_before_giving_up)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static waitForDocumentDeletion(
				final DocsService self, 
				final options=[:],
				String docName,
				String folderName) {
		int maxcount = (options["maxcount"]?options["maxcount"]:10)
		int delay = (options["delay"]?options["delay"]:1000)

		// horrible cludge to wait for google to index the doc 
    	def i=0
    	while(i<maxcount){
    		sleep(delay)

    		def (_, res2) = self.getDocument(docName, folderName)    		
    		if(res2 == GApiStatus.NOT_FOUND) {
    			return [ GApiStatus.OK, i*delay]
    		}
    		i++
    	}
    	return [ GApiStatus.KO, i*delay]
	}	
}
