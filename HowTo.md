# How To #
### Install Eclipse + M2Eclipse & Subclipse & Groovy Eclipse plugin ###


### Checkout the project as a Maven project ###
```
svn checkout http://mandubian-gabilities.googlecode.com/svn/trunk/ mandubian-gabilities-read-only
```


### Edit and update the file src/test/resources/gdata.properties ###
```
username = your_google_username
password = your_goole_password
applicationname=mandubian-gabilitiestest-1
csvfilepath = src/test/resources/cols.csv
docname = GDocAbilityTestDoc3
worksheetname = WorksheetTest
foldername = GDocAbilityTestFolder3
folderdocname = GDocAbilityTestFolderDoc3
```

_The given doc/folder/worsheet names are the names of the documents/folders created by JUnit tests_



### Run "Maven Install" ###
  * It should download libraries from [Groovy Codehaus Repository](http://repository.codehaus.org/org/codehaus/groovy) and from [mandubian-mvn repository](http://code.google.com/p/mandubian-mvn)
  * It should compile
  * It should run 45 JUnit tests

This is a draft with basic functions and it will evolve certainly so don't be surprise!

Now you can go to the [Documentation](Documentation.md) if you like

Play with it, criticize and give ideas to improve it.

That's all folks...