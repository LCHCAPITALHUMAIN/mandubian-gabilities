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

import java.io.File;
import java.util.List;
import java.util.Map;

import groovy.time.BaseDuration
import org.codehaus.groovy.runtime.TimeCategory
import com.google.gdata.client.spreadsheet.SpreadsheetService
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import com.google.gdata.data.spreadsheet.SpreadsheetFeed
import com.google.gdata.data.spreadsheet.WorksheetEntry
import com.google.gdata.data.spreadsheet.WorksheetFeed
import com.google.gdata.client.spreadsheet.WorksheetQuery
import com.google.gdata.client.spreadsheet.ListQuery
import com.google.gdata.client.spreadsheet.CellQuery
import com.google.gdata.data.MediaContent

import com.google.gdata.util.AuthenticationException
import com.google.gdata.util.ServiceException
import com.google.gdata.client.docs.DocsService
import com.google.gdata.data.PlainTextConstruct
import com.google.gdata.data.docs.DocumentListEntry
import com.google.gdata.data.docs.FolderEntry
import com.google.gdata.data.docs.DocumentListFeed

import com.google.gdata.data.spreadsheet.ListEntry
import com.google.gdata.data.spreadsheet.ListFeed
import com.google.gdata.data.spreadsheet.CellEntry
import com.google.gdata.data.spreadsheet.CellFeed

import org.mandubian.google.gabilities.GApiConstants

/**
 * @author mandubian
 *
 */
public class GSpreadsheetAbility{
	/**
	 * gets a Spreadsheet from its name using DocsService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 *  
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1")<br/>
	 * def (spreadsheet) = service.getSpreadsheet("test1")<br/>
	 * def (_, res) = service.getSpreadsheet("test1")<br/>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1", exact:false)<br/>
	 * </code>
	 * @param self				the DocsService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name to create.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							exact: true
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(SpreadsheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(List&lt;com.google.gdata.data.docs.SpreadsheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getSpreadsheet(
			final DocsService self,
			final options = [:],
			final String spreadsheetName)
				throws 	MalformedURLException, IOException, ServiceException {
		use(GDocAbility) {
			return self.getDocument(options, spreadsheetName)
		}
	}	
	
	/**
	 * gets a Spreadsheet from its name using SpreadsheetService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1")<br/>
	 * def (spreadsheet) = service.getSpreadsheet("test1")<br/>
	 * def (_, res) = service.getSpreadsheet("test1")<br/>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1", exact:false)<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name to create.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							exact:true
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 												This is the same as google api "title-exact" query parameter.
	 * 												</td><td>boolean</td><td>true</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(SpreadsheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(List&lt;SpreadsheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getSpreadsheet(
			final SpreadsheetService self,
			final options = [:],
			final String spreadsheetName)
				throws 	MalformedURLException, IOException, ServiceException {		
		def exact = options["exact"]?options["exact"]:true
		
		SpreadsheetFeed feed = 
			self.getFeed(
					new URL("${GApiConstants.feedBaseUrlSpreadsheet}?title=${spreadsheetName}&title-exact=${exact}"), 
					SpreadsheetFeed.class)
					
		switch(feed.getEntries()?.size())
		{
		case 0:
			return [ null, GApiStatus.NOT_FOUND ]
		case 1:
			return [ feed.getEntries()?.get(0), GApiStatus.SINGLE_FOUND ]			
		default:
			return [ feed.getEntries(), GApiStatus.MULTIPLE_FOUND ]
		}			
	}

	/**
	 * gets a Spreadsheet from its key using SpreadsheetService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.getSpreadsheetByKey("987SHSK88342")<br/>
	 * def (spreadsheet) = service.getSpreadsheetByKey("987SHSK88342")<br/>
	 * def (_, res) = service.getSpreadsheetByKey("987SHSK88342")<br/>
	 * def (spreadsheet, res) = service.getSpreadsheetByKey("987SHSK88342")<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param key				the spreadsheet key to get.
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(SpreadsheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(List&lt;SpreadsheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getSpreadsheetByKey(
			final SpreadsheetService self, final String key)
				throws 	MalformedURLException, IOException, ServiceException {			
		SpreadsheetEntry entry = 
			self.getEntry(
					new URL("${GApiConstants.feedBaseUrlSpreadsheet}/${key}"), 
					SpreadsheetEntry.class)
		if(entry==null)
			return [ null, GApiStatus.NOT_FOUND ]
		else return [ entry, GApiStatus.OK ]			
	}
	
	/**
	 * creates a new Spreadsheet using DocsService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1")<br/>
	 * def (spreadsheet) = service.createSpreadsheet("test1")<br/>
	 * def (_, res) = service.createSpreadsheet("test1")<br/>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", force:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the spreadsheet name to create.
	 * @param options		Specifies function options.<br/>
	 * 						For example:<br/>
	 * 						force: true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 								<li>(com.google.gdata.data.docs.SpreadsheetEntry, GApiStatus.OK)</li>
	 * 								<li>(null, GApiStatus.ALREADY_EXISTS)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static createSpreadsheet(
			final DocsService self,
			final options = [:],
			final String docname)
				throws 	MalformedURLException, IOException, ServiceException {
		def force = (options["force"]?options["force"]:false)
		use(GDocAbility) {

			def (doc, res) = self.getDocument(options, docname)
			switch(res)
			{
			case GApiStatus.NOT_FOUND:
				com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
					new com.google.gdata.data.docs.SpreadsheetEntry()
				newEntry.setTitle(new PlainTextConstruct(docname))
	
				return [ self.insert(new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
				         GApiStatus.OK ]
			default:
				if(force) {
					com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
						new com.google.gdata.data.docs.SpreadsheetEntry()
					newEntry.setTitle(new PlainTextConstruct(docname))
	
					return [ self.insert(new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
					         GApiStatus.OK ]
				}
				return [ null, GApiStatus.ALREADY_EXISTS]
			}
		}
	}

	/**
	 * creates a new Spreadsheet in a folder using DocsService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1")<br/>
	 * def (spreadsheet) = service.createSpreadsheet("test1")<br/>
	 * def (_, res) = service.createSpreadsheet("test1")<br/>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", force:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the spreadsheet name to create.
	 * @param foldername	the folder name in which the spreadsheet will be created.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						force:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(null, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(com.google.gdata.data.docs.SpreadsheetEntry, GApiStatus.OK)</li>
	 * 								<li>(null, GApiStatus.ALREADY_EXISTS)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static createSpreadsheet(
			final DocsService self,
			final options = [:],
			final String docname, 
			final String foldername)
				throws 	MalformedURLException, IOException, ServiceException {
		def force = (options["force"]?options["force"]:false)

		use(GDocAbility){
			def(folder, res) = self.getFolder(options, foldername)
			
			if(res == GApiStatus.NOT_FOUND){
				return [ null, GApiStatus.NOT_FOUND ]
			}
			else if(res == GApiStatus.MULTIPLE_FOUND){
				return[ null,  GApiStatus.MULTIPLE_FOUND ]
			}
			
			def (doc, res2) = self.getDocument(options, docname, foldername)
			switch(res2)
			{
			case GApiStatus.NOT_FOUND:
				com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
					new com.google.gdata.data.docs.SpreadsheetEntry()
				newEntry.setTitle(new PlainTextConstruct(docname))
	
				return [ self.insert(
								new URL(((MediaContent)folder.getContent()).getUri()), newEntry), 
				         GApiStatus.OK ]
			default:
				if(force) {
					com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
						new com.google.gdata.data.docs.SpreadsheetEntry()
					newEntry.setTitle(new PlainTextConstruct(docname))
	
					return [ self.insert(
							new URL(((MediaContent)folder.getContent()).getUri()), newEntry), 
							GApiStatus.OK ]
				}
				return [ null, GApiStatus.ALREADY_EXISTS]
			}	
		}
	}

	/**
	 * creates a new Spreadsheet using DocsService and provisions it with the content of 
	 * a file which can be imported by GoogleSpreadsheeAPI (CSV, XLS, etc...).
	 * The file is externally provided and already opened.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", file1)<br/>
	 * def (spreadsheet) = service.createSpreadsheet("test1", file1)<br/>
	 * def (_, res) = service.createSpreadsheet("test1", file1)<br/>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", file1, force:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the spreadsheet name to create.
	 * @param file			the File externally managed.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						force:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 								<li>(com.google.gdata.data.docs.SpreadsheetEntry, GApiStatus.OK)</li>
	 * 								<li>(null, GApiStatus.ALREADY_EXISTS)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static createSpreadsheet(
			final DocsService self,
			final options = [:],
			final String docname, 
			final File file)
				throws 	MalformedURLException, IOException, ServiceException {
		def force = (options["force"]?options["force"]:false)

		use(GDocAbility){
			def (doc, res) = self.getDocument(options, docname)
			switch(res)
			{
			case GApiStatus.NOT_FOUND:
				com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
					new com.google.gdata.data.docs.SpreadsheetEntry()
				newEntry.setTitle(new PlainTextConstruct(docname))
				newEntry.setFile(
					file, 
					DocumentListEntry.getMimeTypeFromFileName(file.getName()))
				return [ self.insert(
								new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
				         GApiStatus.OK ]
			default:
				if(force) {
					com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
						new com.google.gdata.data.docs.SpreadsheetEntry()
					newEntry.setTitle(new PlainTextConstruct(docname))
					newEntry.setFile(
						file, 
						DocumentListEntry.getMimeTypeFromFileName(file.getName()))
					return [ self.insert(
								new URL(GApiConstants.feedBaseUrlDoc), newEntry), 
							GApiStatus.OK ]
				}
				return [ null, GApiStatus.ALREADY_EXISTS]
			}	
		}
	}
	
	/**
	 * creates a new Spreadsheet using DocsService in a folder and provisions it with 
	 * the content of a file which can be imported by GoogleSpreadsheeAPI 
	 * (CSV, XLS, etc...).
	 * The file is externally provided and already opened.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", "folder1", file1)<br/>
	 * def (spreadsheet) = service.createSpreadsheet("test1", "folder1", file1)<br/>
	 * def (_, res) = service.createSpreadsheet("test1", "folder1", file1)<br/>
	 * def (spreadsheet, res) = service.createSpreadsheet("test1", file1, force:true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docname		the spreadsheet name to create.
	 * @param foldername	the folder name to create.
	 * @param file			the File externally managed.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						force:tru
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 							<li>(null, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							<li>(com.google.gdata.data.docs.SpreadsheetEntry, GApiStatus.OK)</li>
	 * 							<li>(null, GApiStatus.ALREADY_EXISTS)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static createSpreadsheet(
		final DocsService self, 
		final options = [:],
		final String docname, 
		final String foldername, 
		final File file)
			throws 	MalformedURLException, IOException, ServiceException {
		def force = (options["force"]?options["force"]:false)
		
		use(GDocAbility){
			def(folder, res) = self.getFolder(options, foldername)
			if(res == GApiStatus.NOT_FOUND){
				return [ null, GApiStatus.NOT_FOUND ]
			}
			else if(res == GApiStatus.MULTIPLE_FOUND){
				return[ null,  GApiStatus.MULTIPLE_FOUND ]
			}
			
			def (doc, res2) = self.getDocument(options, docname, foldername)
			switch(res2)
			{
			case GApiStatus.NOT_FOUND:
				com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
					new com.google.gdata.data.docs.SpreadsheetEntry()
				newEntry.setTitle(new PlainTextConstruct(docname))
				newEntry.setFile(
					file, 
					DocumentListEntry.getMimeTypeFromFileName(file.getName()))
					
				return [ self.insert(
								new URL(GApiConstants.feedBaseUrlFolder
									+'/'+folderEntry.getResourceId()), newEntry), 
								GApiStatus.OK ]
			default:
				if(force) {
					com.google.gdata.data.docs.SpreadsheetEntry newEntry = 
						new com.google.gdata.data.docs.SpreadsheetEntry()
					newEntry.setTitle(new PlainTextConstruct(docname))
					newEntry.setFile(
						file, 
						DocumentListEntry.getMimeTypeFromFileName(file.getName()))
						
					return [ self.insert(
								new URL(GApiConstants.feedBaseUrlFolder
									+'/'+folderEntry.getResourceId()), newEntry), 
								GApiStatus.OK ]
				}
				return [ null, GApiStatus.ALREADY_EXISTS]
			}	
		}
	}	

	/**
	 * deletes one or several Spreadsheet(s) using DocsService.
	 * The result is a GApiResult and not a tuple.<br/>
	 * For example:<br/>
	 * <code>
	 * def res = service.deleteSpreadsheet("test1")<br/>
	 * def res = service.deleteSpreadsheet("test1", multiple: true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param spreadsheetName the spreadsheet name to delete.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						multiple:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a GApiResult among: 
	 * 						<ul>
	 * 							<li>NOT_FOUND</li>
	 * 							<li>SINGLE_DELETED</li>
	 * 							<li>MULTIPLE_DELETED</li>
	 * 							<li>MULTIPLE_FOUND</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static deleteSpreadsheet(
			final DocsService self,
			final options = [:],
			final String spreadsheetName)
				throws 	MalformedURLException, IOException, ServiceException {		
		use(GDocAbility){
			return self.deleteDocument(options, spreadsheetName)
		}
	}

	/**
	 * deletes one or several Spreadsheets(s) in a folder.
	 * The result is a GApiResult and not a tuple.<br/>
	 * For example:<br/>
	 * <code>
	 * def res = service.deleteSpreadsheet("test1", "folder1")<br/>
	 * def res = service.deleteSpreadsheet("test1", "folder1", multiple: true)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name to delete.
	 * @param folderName	the folder name to look for.
	 * @param options		Specifies function options as a map.<br/>
	 * 						For example:<br/>
	 * 						multiple:true
	 * 						Accepted options are :<br/>
	 * 						<table>
	 * 							<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 							<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 						</table>
	 * @return				a GApiResult among: 
	 * 						<ul>
	 * 							<li>NOT_FOUND</li>
	 * 							<li>SINGLE_DELETED</li>
	 * 							<li>MULTIPLE_DELETED</li>
	 * 							<li>MULTIPLE_FOUND</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */	
	public static deleteSpreadsheet(
			final DocsService self,
			final options = [:],
			final String spreadsheetName, 
			final String folderName)
				throws 	MalformedURLException, IOException, ServiceException {		
		use(GDocAbility){
			return self.deleteDocument(options, spreadsheetName, folderName)
		}
	}
	
	/**
	 * gets a Spreadsheet in a folder from its name using DocsService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Warning: the returned spreadsheet is a com.google.gdata.data.docs.SpreadsheetEntry
	 * instance and not a com.google.gdata.data.spreadsheet.SpreadsheetEntry</i>
	 * For example:<br/>
	 * <code>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1", "folder1")<br/>
	 * def (spreadsheet) = service.getSpreadsheet("test1", "folder1")<br/>
	 * def (_, res) = service.getSpreadsheet("test1", "folder1")<br/>
	 * def (spreadsheet, res) = service.getSpreadsheet("test1", "folder1", [ "exact": false])<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name to create.
	 * @param folderName		the folder name in which to create.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							[ "exact": true ]
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(com.google.gdata.data.docs.SpreadsheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(List&lt;com.google.gdata.data.docs.SpreadsheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getSpreadsheet(
			final DocsService self,
			final options = [:],
			final String spreadsheetName, 
			final String folderName)
				throws 	MalformedURLException, IOException, ServiceException {		
		use(GDocAbility){
			return self.getDocument(options, spreadsheetName, folderName)
		}		
	}

	
	/**
	 * converts GDocAPI spreadsheet com.google.gdata.data.docs.SpreadsheetEntry into 
	 * GSpreadsheetAPI com.google.gdata.data.spreadsheet.SpreadsheetEntry.
	 * For example:<br/>
	 * <code>
	 * def spreadsheet = service.convertSpreadsheetFromDoc(docSpreadsheet)<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param spreadsheet		the com.google.gdata.data.docs.SpreadsheetEntry name to convert.
	 * @return					com.google.gdata.data.spreadsheet.SpreadsheetEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static SpreadsheetEntry convertSpreadsheetFromDoc(
			final SpreadsheetService self, 
			final com.google.gdata.data.docs.DocumentListEntry spreadsheet)
				throws 	MalformedURLException, IOException, ServiceException {	
		//def key = spreadsheet.getKey()
		//def realKey = key.substring("spreadsheet%3A".length())
		def resid = spreadsheet.getResourceId()
		def key = resid.substring("spreadsheet:".length())
		println("resid:${resid}")
		println("key:${GApiConstants.feedBaseUrlSpreadsheet}/${key}")
		return self.getEntry(
					new URL("${GApiConstants.feedBaseUrlSpreadsheet}/${key}"), 
					SpreadsheetEntry.class)		
	}

	/**
	 * converts GSpreadsheetAPI com.google.gdata.data.spreadsheet.SpreadsheetEntry 
	 * into GDocAPI spreadsheet com.google.gdata.data.docs.SpreadsheetEntry. 
	 * For example:<br/>
	 * <code>
	 * def docSpreadsheet = service.convertSpreadsheetToDoc(spreadsheet)<br/>
	 * </code>
	 * @param self				the DocsService externally initialized and authenticated.
	 * @param spreadsheet		the com.google.gdata.data.spreadsheet.SpreadsheetEntry  name to convert.
	 * @return					com.google.gdata.data.docs.SpreadsheetEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static com.google.gdata.data.docs.SpreadsheetEntry convertSpreadsheetToDoc(
			final DocsService self, 
			final com.google.gdata.data.spreadsheet.SpreadsheetEntry spreadsheet)
				throws 	MalformedURLException, IOException, ServiceException {	
		return self.getEntry(
					new URL("${GApiConstants.feedBaseUrlDoc}/spreadsheet:${spreadsheet.getKey()}"), 
					com.google.gdata.data.docs.SpreadsheetEntry.class)		
	}
	
	/**
	 * waits for spreadsheet creation by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1")<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the document name to look for.
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
	public static waitForSpreadsheetCreation(
				final DocsService self,
				final options = [:],
				String docName) {
		use(GDocAbility){
			return self.waitForDocumentCreation(options, docName)
		}
	}	

	/**
	 * waits for spreadsheet creation in a folder by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", "folder1")<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", "folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", "folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetCreation("doc1", "folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the document name to look for.
	 * @param folderName	the folder name to look for.
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
	public static waitForSpreadsheetCreation(
				final DocsService self,
				final options = [:],
				String docName,
				String folderName) {
		use(GDocAbility){
			return self.waitForDocumentCreation(options, docName, folderName)
		}
	}
	
	/**
	 * waits for spreadsheet deletion by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1")<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the document name to look for.
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
	public static waitForSpreadsheetDeletion(
				final DocsService self,
				final options = [:],
				String docName) {
		use(GDocAbility){
			return self.waitForDocumentDeletion(options, docName)
		}
	}	

	/**
	 * waits for spreadsheet deletion in a folder by interrogating the DocsService periodically for this folder 
	 * until it is found and sleeping between 2 tries... (not very elegant :) ) 
	 * The result is a tuple [GApiStatus, number_of_milliseconds].<br/>
	 * For example:<br/>
	 * <code>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", "folder1")<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", "folder1", maxcount:20, delay:2000)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", "folder1", maxcount:20)<br/>
	 * def (res, nb_ms) = service.waitForSpreadsheetDeletion("doc1", "folder1", delay:2000)<br/>
	 * </code>
	 * @param self			the DocsService externally initialized and authenticated.
	 * @param docName		the document name to look for.
	 * @param folderName	the folder name to look for.
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
	public static waitForSpreadsheetDeletion(
				final DocsService self,
				final options = [:],
				String docName,
				String folderName) {
		use(GDocAbility){
			return self.waitForDocumentDeletion(options, docName, folderName)
		}
	}	

	
	/**
	 * gets one or more Worksheet(s) from a spreadsheet entry and from the 
	 * worksheet name using SpreadsheetService.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (worksheet, res) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * def (worksheet) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * def (_, res) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * </code>
	 * @param self			the SpreadsheetEntry externally initialized.
	 * @param worksheetName	the worksheet name to get.
	 * @param options		Specifies function options as a map.<br/>
	 * 						<i>No option recognized for the time being</i>
	 * @return				a tuple among: 
	 * 						<ul>
	 * 							<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 							<li>(WorksheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							<li>(List&lt;WorksheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 						</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getWorksheet(
			final SpreadsheetEntry self,  
			final options = [:],
			final String worksheetName)
				throws 	MalformedURLException, IOException, ServiceException {		
		def itList = []
        self.getWorksheets().each {
			if(it.getTitle().getPlainText() == worksheetName) itList << it
		}

		switch(itList.size()){
		case 0: return [ null, GApiStatus.NOT_FOUND ]
		case 1: return [ itList[0], GApiStatus.SINGLE_FOUND ]
        default: return [ itList, GApiStatus.MULTIPLE_FOUND ]
		}
	}



	
	/**
	 * gets a Worksheet from spreadsheet name and worksheet name using SpreadsheetService.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (worksheet, res) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (worksheet) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (_, res) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (worksheet, res) = service.getWorksheet("spreadsheet1", "worksheet1", exact:false)<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name containing the worksheet.
	 * @param worksheetName		the worksheet name to get.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							exact: true
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>exact</th><td>specifies whether the title query should be taken as an exact string.
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(List&lt;WorksheetEntry&gt;, GApiStatus.SINGLE_FOUND)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static getWorksheet(
			final SpreadsheetService self,
			final options = [:],
			final String spreadsheetName, 
			final String worksheetName)
				throws 	MalformedURLException, IOException, ServiceException {
		def exact = options["exact"] ? options["exact"]:true;

		def (spreadsheet, res) = self.getSpreadsheet(options, spreadsheetName)
		if(res == GApiStatus.NOT_FOUND){
			return [ null, GApiStatus.NOT_FOUND ]
		}
		else if(res == GApiStatus.MULTIPLE_FOUND){
			return [ null, GApiStatus.MULTIPLE_FOUND ]
		}
		
		WorksheetQuery query = new WorksheetQuery(spreadsheet.getWorksheetFeedUrl())
		query.setTitleExact(exact)
		query.setTitleQuery(worksheetName)
		
        WorksheetFeed feed = 
        	self.getFeed(
        			query,
        			WorksheetFeed.class)
		switch(feed.getEntries()?.size()){
		case 0: return [ null, GApiStatus.NOT_FOUND ]
		case 1: return [ feed.getEntries().get(0), GApiStatus.SINGLE_FOUND ]
        default: return [ feed.getEntries(), GApiStatus.MULTIPLE_FOUND ]
		}

	}


	/**
	 * adds a Worksheet to a spreadsheet from spreadsheet name and worksheet name
	 * using SpreadsheetService.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (worksheet, res) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (worksheet) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (_, res) = service.getWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def (worksheet, res) = service.getWorksheet("spreadsheet1", "worksheet1", exact:false)<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized and authenticated.
	 * @param spreadsheetName	the spreadsheet name containing the worksheet.
	 * @param worksheetName		the worksheet name to get.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							force: true
	 * 							rows:25
	 * 							cols:10
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 								<tr><th>rows</th><td>sets the number of rows in the worksheet</td><td>integer</td><td>100</td></tr>
	 * 								<tr><th>cols</th><td>sets the number of cols in the worksheet</td><td>integer</td><td>20</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(null, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.ALREADY_EXISTS)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static addWorksheet(
			final SpreadsheetService self,
			final options = [:],
			final String spreadsheetName, 
			final String worksheetName) 
				throws 	MalformedURLException, IOException, ServiceException {
		boolean force = options["force"] ? options["force"]:false;
		int rows = (options["rows"]?options["rows"]:100)
		int cols = (options["cols"]?options["cols"]:20)
		
		def (spreadsheet, res) = self.getSpreadsheet(options, spreadsheetName)
		if(res == GApiStatus.NOT_FOUND){
			return [ null, GApiStatus.NOT_FOUND ]
		}
		else if(res == GApiStatus.MULTIPLE_FOUND){
			return [ null, GApiStatus.MULTIPLE_FOUND ]
		}

		def (worksheet, res2) = spreadsheet.getWorksheet(worksheetName)
		switch(res2)
		{
		case GApiStatus.NOT_FOUND:
			WorksheetEntry newEntry = new WorksheetEntry()
			newEntry.setTitle(new PlainTextConstruct(worksheetName))
			newEntry.setRowCount(rows)
			newEntry.setColCount(cols)

			return [ self.insert(spreadsheet.getWorksheetFeedUrl(), newEntry), 
			         GApiStatus.OK ]
		default:
			if(force){
				WorksheetEntry newEntry = new WorksheetEntry()
				newEntry.setTitle(new PlainTextConstruct(worksheetName))
				newEntry.setRowCount(rows)
				newEntry.setColCount(cols)

				return [ self.insert(spreadsheet.getWorksheetFeedUrl(), newEntry),
				         GApiStatus.OK ]
			}
			return [ null, GApiStatus.ALREADY_EXISTS ]
		}
	}

	/**
	 * adds a Worksheet to a spreadsheet from worksheet name.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def (worksheet, res) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * def (worksheet) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * def (_, res) = spreadsheet.getWorksheet("worksheet1")<br/>
	 * def (worksheet, res) = spreadsheet.getWorksheet("worksheet1", exact:false)<br/>
	 * </code>
	 * @param self				the SpreadsheetEntry externally initialized and authenticated.
	 * @param worksheetName		the worksheet name to get.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							force: true, rows:125, cols:45
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the creation when an existing spreadsheet with the same name is found</td><td>boolean</td><td>false</td></tr>
	 * 								<tr><th>rows</th><td>sets the number of rows in the worksheet</td><td>integer</td><td>100</td></tr>
	 * 								<tr><th>cols</th><td>sets the number of cols in the worksheet</td><td>integer</td><td>20</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(null, GApiStatus.MULTIPLE_FOUND)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.ALREADY_EXISTS)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static addWorksheet(
			final SpreadsheetEntry self,
			final options = [:],
			final String worksheetName) 
				throws 	MalformedURLException, IOException, ServiceException {
		boolean force = options["force"] ? options["force"]:false;
		int rows = (options["rows"]?options["rows"]:100)
		int cols = (options["cols"]?options["cols"]:20)

		def (worksheet, res) = self.getWorksheet(options, worksheetName)
		switch(res)
		{
		case GApiStatus.NOT_FOUND:
			WorksheetEntry newEntry = new WorksheetEntry()
			newEntry.setTitle(new PlainTextConstruct(worksheetName))
			newEntry.setRowCount(rows)
			newEntry.setColCount(cols)

			return [ self.getService().insert(self.getWorksheetFeedUrl(), newEntry), 
			         GApiStatus.OK ]
		default:
			if(force){
				WorksheetEntry newEntry = new WorksheetEntry()
				newEntry.setTitle(new PlainTextConstruct(worksheetName))
				newEntry.setRowCount(rows)
				newEntry.setColCount(cols)

				return [ self.getService().insert(self.getWorksheetFeedUrl(), newEntry),
				         GApiStatus.OK ]
			}
			return [ null, GApiStatus.ALREADY_EXISTS ]
		}
	}

	/**
	 * deletes a Worksheet from a spreadsheet searching for spreadsheet name and 
	 * worksheet name.
	 * For example:<br/>
	 * <code>
	 * def res = service.deleteWorksheet("spreadsheet1", "worksheet1")<br/>
	 * def res = service.deleteWorksheet("spreadsheet1", "worksheet1", multiple:true)<br/>
	 * </code>
	 * @param self				the SpreadsheetService externally initialized.
	 * @param spreadsheetName	the spreadsheet name containing the worksheet.
	 * @param worksheetName		the worksheet name to get.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							multiple":true
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a GApiStatus among: 
	 * 							<ul>
	 * 								<li>GApiStatus.NOT_FOUND</li>
	 * 								<li>ApiStatus.SINGLE_DELETED</li>
	 * 								<li>ApiStatus.MULTIPLE_DELETED</li>
	 * 								<li>ApiStatus.MULTIPLE_FOUND</li>
	 * 								<li>ApiStatus.BAD_STATE</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static deleteWorksheet(
			final SpreadsheetService self,
			final options = [:],
			final String spreadsheetName, 
			final String worksheetName) 
				throws 	MalformedURLException, IOException, ServiceException {
		def multiple = options["multiple"] ? options["multiple"]:false;

		def (worksheet, res) = self.getWorksheet(options, spreadsheetName, worksheetName)
		switch(res){
		case GApiStatus.NOT_FOUND: return [ GApiStatus.NOT_FOUND, 0 ]
		
		case GApiStatus.SINGLE_FOUND:
			worksheet.delete()
			return [ GApiStatus.SINGLE_DELETED, 1 ]
			
		case GApiStatus.MULTIPLE_FOUND:
			if(multiple) {
				worksheet.each { it.delete() }
				return [ GApiStatus.MULTIPLE_DELETED, worksheet.size() ]
			}
			return [ GApiStatus.MULTIPLE_FOUND, worksheet.size() ]
			
		default: return [ GApiStatus.BAD_STATE, 0 ]
		}
	}

	/**
	 * deletes a Worksheet from a spreadsheet searching for worksheet name.
	 * For example:<br/>
	 * <code>
	 * def res = spreadsheet.deleteWorksheet("worksheet1")<br/>
	 * def res = spreadsheet.deleteWorksheet("worksheet1", multiple:true)<br/>
	 * </code>
	 * @param self				the SpreadsheetEntry externally initialized.
	 * @param worksheetName		the worksheet name to get.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							multiple:true
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>multiple</th><td>Allows deleting multiple found folders or not</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a GApiStatus among: 
	 * 							<ul>
	 * 								<li>GApiStatus.NOT_FOUND</li>
	 * 								<li>ApiStatus.SINGLE_DELETED</li>
	 * 								<li>ApiStatus.MULTIPLE_DELETED</li>
	 * 								<li>ApiStatus.MULTIPLE_FOUND</li>
	 * 								<li>ApiStatus.BAD_STATE</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static deleteWorksheet(
			final SpreadsheetEntry self,
			final options = [:],
			final String worksheetName) 
				throws 	MalformedURLException, IOException, ServiceException {
		def multiple = options["multiple"] ? options["multiple"]:false;
		
		def (worksheet, res) = self.getWorksheet(options, worksheetName)
		switch(res){
		case GApiStatus.NOT_FOUND: return [GApiStatus.NOT_FOUND, 0 ]
		
		case GApiStatus.SINGLE_FOUND:
			worksheet.delete()
			return [ GApiStatus.SINGLE_DELETED, 1 ]
			
		case GApiStatus.MULTIPLE_FOUND:
			if(multiple) {
				worksheet.each { it.delete() }
				return [ GApiStatus.MULTIPLE_DELETED, worksheet.size() ]
			}
			return [ GApiStatus.MULTIPLE_FOUND, worksheet.size() ]
			
		default: return [ GApiStatus.BAD_STATE, 0 ]
		}		
	}	

	/**
	 * changes worksheet metadata title.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * For example:<br/>
	 * <code>
	 * def res = worksheet.changeTitle("new-title")<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param title				the new title of the worksheet.
	 * @param options			Specifies function options as a map.<br/>
	 * 							No Options accepted yet. Here for further use and homogeneity
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static changeTitle(
			final WorksheetEntry self,  final String title) 
				throws 	MalformedURLException, IOException, ServiceException {
		self.setTitle(new PlainTextConstruct(title))
		return [ self.update(), GApiStatus.OK ]		
	}

	/**
	 * changes worksheet metadata row count.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Take into account that if the new rowcount is less than the previous,
	 * you may loose some data so by default, this case will return a DATA_LOSS_RISK
	 * error but you can use the options["force"] to force the change.</i><br/> 
	 * For example:<br/>
	 * <code>
	 * def res = worksheet.changeRowCount(125)<br/>
	 * def res = worksheet.changeRowCount(125, [ "force": true ])<br/>
	 * 
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param rows				the new number of rows.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							[ "force": true ]
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the change of row count even if it implies losing data</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.DATA_LOSS_RISK)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static changeRowCount(
			final WorksheetEntry self,  
			final options = [:],			
			final int rows) 
				throws 	MalformedURLException, IOException, ServiceException {
		def force = options["force"]?options["force"]:false
		if(rows < self.getRowCount()&&!options["force"]) 
			return [ self, GApiStatus.DATA_LOSS_RISK ]

		self.setRowCount(rows)
		return [ self.update(), GApiStatus.OK ]
	}

	/**
	 * changes worksheet metadata col count.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Take into account that if the new colcount is less than the previous,
	 * you may loose some data so by default, this case will return a DATA_LOSS_RISK
	 * error but you can use the options["force"] to force the change.</i><br/> 
	 * For example:<br/>
	 * <code>
	 * def res = worksheet.changeColCount(125)<br/>
	 * def res = worksheet.changeColCount(125, [ "force": true ])<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param cols				the new number of columns.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							[ "force": true ]
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the change of row count even if it implies losing data</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.DATA_LOSS_RISK)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static changeColCount(
			final WorksheetEntry self,  
			final options = [:],			
			final int cols) 
				throws 	MalformedURLException, IOException, ServiceException {
		def force = options["force"]?options["force"]:false
		if(cols < self.getColCount()&&!options["force"]) 
			return [ self, GApiStatus.DATA_LOSS_RISK ]

		self.setColCount(cols)
		return [ self.update(), GApiStatus.OK ]
	}

	/**
	 * changes worksheet metadata row/col counts.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Take into account that if the new row/colcount is less than the previous,
	 * you may loose some data so by default, this case will return a DATA_LOSS_RISK
	 * error but you can use the options["force"] to force the change.</i><br/> 
	 * For example:<br/>
	 * <code>
	 * def res = worksheet.changeRowColCount(125, 24)<br/>
	 * def res = worksheet.changeRowColCount(125, 26, [ "force": true ])<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param rows				the new number of rows. 
	 * @param cols				the new number of columns.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							[ "force": true ]
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the change of row count even if it implies losing data</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.DATA_LOSS_RISK)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static changeRowColCount(
			final WorksheetEntry self,  
			final options = [:],		
			final int rows, final int cols) 
				throws 	MalformedURLException, IOException, ServiceException {
		def force = options["force"]?options["force"]:false
        if(rows < self.getRowCount()&&!options["force"]) 
 			return [ self, GApiStatus.DATA_LOSS_RISK ]
		if(cols < self.getColCount()&&!options["force"]) 
			return [ self, GApiStatus.DATA_LOSS_RISK ]
        
 		self.setRowCount(rows)
		self.setColCount(cols)
		return [ self.update(), GApiStatus.OK ]
	}

	/**
	 * changes worksheet metadata title/rowcount/colcount.
	 * <i>Take into account the result is a tuple (WorksheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <i>Take into account that if the new row/colcount is less than the previous,
	 * you may loose some data so by default, this case will return a DATA_LOSS_RISK
	 * error but you can use the options["force"] to force the change.</i><br/> 
	 * For example:<br/>
	 * <code>
	 * def res = worksheet.changeMetadata("newtitle", 125, 24)<br/>
	 * def res = worksheet.changeMetadata("newtitle", 125, 26, [ "force": true ])<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param title				the new title. 
	 * @param rows				the new number of rows. 
	 * @param cols				the new number of columns.
	 * @param options			Specifies function options as a map.<br/>
	 * 							For example:<br/>
	 * 							[ "force": true ]
	 * 							Accepted options are :<br/>
	 * 							<table>
	 * 								<tr><th>option</th><th>description</th><th>type</th><th>default value</th>
	 * 								<tr><th>force</th><td>forces or not the change of row count even if it implies losing data</td><td>boolean</td><td>false</td></tr>
	 * 							</table>
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(WorksheetEntry, GApiStatus.OK)</li>
	 * 								<li>(WorksheetEntry, GApiStatus.DATA_LOSS_RISK)</li>
	 * 							</ul>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static changeMetadata(
			final WorksheetEntry self,  
			final options = [:],		
			final title, final int rows, final int cols) 
				throws 	MalformedURLException, IOException, ServiceException {
		def force = options["force"]?options["force"]:false
		if(rows < self.getRowCount()&&!options["force"]) 
 			return [ self, GApiStatus.DATA_LOSS_RISK ]
		if(cols < self.getColCount()&&!options["force"]) 
			return [ self, GApiStatus.DATA_LOSS_RISK ]
        
		self.setTitle(new PlainTextConstruct(title))
		self.setRowCount(rows)
		self.setColCount(cols)
		
		return [ self.update(), GApiStatus.OK ]
	}



	
	/**
	 * queries rows in a worksheet with a structured query.
	 * <i>Take into account the result is a tuple (SpreadsheetEntry, GApiStatus) 
	 * and not a single variable.</i><br/>
	 * <code>
	 * def (entry, res) = worksheet.queryRows("name=XXX and age>25")<br/>
	 * def (entry) = worksheet.queryRows("name=XXX and age>25")<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param qyeryStr			the query string. 
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>(null, GApiStatus.NOT_FOUND)</li>
	 * 								<li>(ListEntry, GApiStatus.SINGLE_FOUND)</li>
	 * 								<li>(List<ListEntry>, GApiStatus.MULTIPLE_FOUND)</li>
	 * 							</ul>	 
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static queryRows(
			final WorksheetEntry self,  final String queryStr) 
				throws 	MalformedURLException, IOException, ServiceException
	{
		ListQuery query = new ListQuery(self.getListFeedUrl())
		query.setSpreadsheetQuery(queryStr)
		ListFeed feed = self.getService().query(query, ListFeed.class)

		switch(feed.getEntries()?.size()){
		case 0: return [ null, GApiStatus.NOT_FOUND ]
		case 1: return [ feed.getEntries().get(0), GApiStatus.SINGLE_FOUND ]
        default: return [ feed.getEntries(), GApiStatus.MULTIPLE_FOUND ]
		}
	}

		
	/**
	 * deletes rows in a worksheet with a structured query.
	 * <code>
	 * def (entry, res) = worksheet.queryRows("name=XXX and age>25")<br/>
	 * def (entry) = worksheet.queryRows("name=XXX and age>25")<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param qyeryStr			the query string. 
	 * @return					a tuple among: 
	 * 							<ul>
	 * 								<li>GApiStatus.NOT_FOUND)</li>
	 * 								<li>GApiStatus.SINGLE_DELETED)</li>
	 * 								<li>GApiStatus.MULTIPLE_DELETED)</li>
	 * 							</ul>	 
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static deleteRows(
			final WorksheetEntry self,  final String queryStr) 
				throws 	MalformedURLException, IOException, ServiceException {
		def (rows, res) = self.queryRows(queryStr)
		
		switch(res){
		case GApiStatus.NOT_FOUND: return GApiStatus.NOT_FOUND
		case GApiStatus.SINGLE_FOUND:
			rows.delete()
			return GApiStatus.SINGLE_DELETED
		case GApiStatus.MULTIPLE_FOUND:
			rows.each{ it.delete() }
			return GApiStatus.MULTIPLE_DELETED
		}
	}

	/**
	 * appends a row in a worksheet after the last row filling columns with 
	 * values from a map.
	 * <code>
	 * def res = worksheet.appendRow([ "col1":"value1", "col2":"value2"...)<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param map				the map containing values for columns. 
	 * @return					new ListEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry appendRow(
			final WorksheetEntry self,  final Map map) 
				throws 	MalformedURLException, IOException, ServiceException {
		ListEntry newEntry = new ListEntry();
		
		map.each { key, value -> 
			newEntry.getCustomElements().setValueLocal(key, "${value}")
		}
		
		return self.getService().insert(self.getListFeedUrl(), newEntry);
	}
	
	/**
	 * update a row (ListEntry)filling columns with values from a map.
	 * <code>
	 * def listEntry = listEntry.update([ "col1":"value1", "col2":"value2"...)<br/>
	 * </code>
	 * @param self				the ListEntry externally initialized.
	 * @param map				the map containing values for columns. 
	 * @return					updated ListEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry update(
			final ListEntry self,  final Map map) 
				throws 	MalformedURLException, IOException, ServiceException {	
		map.each { key, value -> 
			self.getCustomElements().setValueLocal(key, "${value}")
		}
		
		return self.update();
	}

	
	/**
	 * gets a cell in a worksheet from its row and col numbers.
	 * <code>
	 * def cellEntry = worksheet.update([ "col1":"value1", "col2":"value2"...)<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param row				the row of the cell. 
	 * @param col				the col of the cell. 
	 * @return					updated CellEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry getCell(
			final WorksheetEntry self,  final int row, final int col) 
				throws 	MalformedURLException, IOException, ServiceException {	
		return self.getService().getEntry(
				new URL(self.getCellFeedUrl().toExternalForm()+"/R"+row+"C"+col), 
				CellEntry.class)		
	}

	/**
	 * updates a cell content in a worksheet from its row and col numbers.
	 * <code>
	 * def cellEntry = worksheet.updateCell(row, col, "content")<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param row				the row of the cell. 
	 * @param col				the col of the cell. 
	 * @param content			the new content of the cell.  
	 * @return					updated CellEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry updateCell(
			final WorksheetEntry self,  final int row, final int col, 
			final String content) 
				throws 	MalformedURLException, IOException, ServiceException {	
		CellEntry cell = self.getCell(row, col)

		cell?.changeInputValueLocal(content)
		return cell?.update()
	}
	
	/**
	 * updates a cellentry content in a worksheet.
	 * <code>
	 * def cellEntry = cellEntry.update("content")<br/>
	 * </code>
	 * @param self				the CellEntry externally initialized.
	 * @param content			the new content of the cell.  
	 * @return					updated CellEntry
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry update(
			final CellEntry self,  final String content) 
				throws 	MalformedURLException, IOException, ServiceException
	{	
		self.changeInputValueLocal(content)
		return self.update()
	}
	
	/**
	 * gets cells in a square [min-row, max-row, min-col, max-col].
	 * If min-row or min-col is not provided, it means there is no minimum 
	 * (from first row or column)
	 * If max-row or max-col is not provided, it means there is no maximum 
	 * (up to last row or column)
	 * <code>
	 * def cellEntries = worksheetEntry.getCells([ "min-row":2, "max-row":25, "min-col":3, "max-col":38 ])<br/>
	 * def cellEntries = worksheetEntry.getCells([ "min-row":2, "min-col":3,  ])<br/>
	 * def cellEntries = worksheetEntry.getCells([ "max-row":25, "min-col":3, "max-col":38 ])<br/>
	 * def cellEntries = worksheetEntry.getCells([ "min-row":2, "max-row":25, "min-col":3])<br/>
	 * </code>
	 * @param self				the WorksheetEntry externally initialized.
	 * @param limits			a map containing defining the limits of the square ("min-row", "max-row", "min-col", "max-col")  
	 * @return					List<CellEntry>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<CellEntry> getCells(
			final WorksheetEntry self,  
			final limits = []) 
				throws 	MalformedURLException, IOException, ServiceException {	
		CellQuery query = new CellQuery(self.getCellFeedUrl());
		query.setMinimumRow(limits["min-row"]);
		query.setMaximumRow(limits["max-row"]);
		query.setMinimumCol(limits["min-col"]);
		query.setMaximumCol(limits["max-col"]);
		return self.getService().query(query, CellFeed.class)?.getEntries();
	}

	/**
	 * gets the cells in the header row being the first row used by GSpreadsheet 
	 * to define columns.
	 * <code>
	 * def cellEntries = worksheetEntry.getHeaderRow()<br/>
	 * </code>
	 * @param self		the WorksheetEntry externally initialized.
	 * @return			List<CellEntry>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<CellEntry> getHeaderRow(	final WorksheetEntry self) 
				throws 	MalformedURLException, IOException, ServiceException
	{	
		return self.getCells([ "max-row": 1 ])
	}

	/**
	 * appends a column to a worksheet after the last column
	 * <code>
	 * def cellEntries = worksheetEntry.appendCol("newcol")<br/>
	 * </code>
	 * @param self		the WorksheetEntry externally initialized.
	 * @param colName	the new column name.
	 * @return			List<CellEntry>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static appendCol(final WorksheetEntry self,  final String colName) 
				throws 	MalformedURLException, IOException, ServiceException 
	{	
		List<CellEntry> cells = self.getHeaderRow()
		
		if(cells.find { it.getPlainTextContent() == colName }) 
				return GApiStatus.ALREADY_EXISTS
		
		def newCol = self.updateCell(1, cells.size()+1, colName)
		return GApiStatus.OK
	}	

	/**
	 * gets the cells in a worksheet column
	 * <code>
	 * def cellEntries = worksheetEntry.getColCells("newcol")<br/>
	 * </code>
	 * @param self		the WorksheetEntry externally initialized.
	 * @param colName	the column name.
	 * @return			List<CellEntry>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<CellEntry> getColCells(
			final WorksheetEntry self,  final String colName) 
				throws 	MalformedURLException, IOException, ServiceException
	{	
		List<CellEntry> cells = self.getHeaderRow()
		def idx=cells.findIndexOf { it.getPlainTextContent() == colName }
		if(idx>-1)
			return self.getCells([ "min-col" : idx+1, "max-col" : idx+1, "min-row": 2 ])
		else return null
	}	

	
	/**
	 * gets the cells in a worksheet row
	 * <code>
	 * def cellEntries = worksheetEntry.getRowCells(5)<br/>
	 * </code>
	 * @param self		the WorksheetEntry externally initialized.
	 * @param row		the row index.
	 * @return			List<CellEntry>
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<CellEntry> getRowCells(
			final WorksheetEntry self,  final int row) 
				throws 	MalformedURLException, IOException, ServiceException
	{	
		return self.getCells([ "min-row": row, "max-row": row])
	}		
	
}
