INSERT INTO Titles (isbn, title, editionNumber, year, publisherID, price) 
VALUES
	("0984782869", "Cracking the Coding Interview: 189 Programming Questions and Solutions", 6, "2015", 1, 34.89),
	("1118431111", "Big Java Early Objects", 5, "2013", 2, 132.77),
	("0439064872", "Harry Potter and the Chamber of Secrets" , 1, "2000", 3, 6.79),
	("0553593714", "A Game of Thrones" , 1, "2011", 4, 6.29),
	("1449355730" , "Learning Python" , 5, "2013", 5, 58.58),
	("0134706056" , "Android Programming: The Big Nerd Ranch Guide" , 3, "2017", 6, 31.15),
	("054792819X", "Lord of the Rings: Return of the King" , 1, "2012", 7, 10.48),
	("0547928203", "Lord of the Rings: The Two Towers", 1, "2012", 7, 9.00),
	("0547928211", "Lord of the Rings: The Fellowship of the Ring", 1, "2012", 7, 8.00),
	("1118531647" , "JavaScript and JQuery: Interactive Front-End Web Development" , 1, "2014", 8, 24.01),
	("0132350882" , "Clean Code: A Handbook of Agile Software Craftsmanship" , 1, "2008", 9, 28.66),
	("0201485672", "Refactoring: Improving the Design of Existing Code", 1, "1999", 10, 44.33),
	("0131177052", "Working Effectively with Legacy Code", 1, 2004, 11, 60.46),
	("1974492559", "Confessions of a Software Techie: The Surprising Truth about Things that Really Matter", 1, 2017, 12, 5.99),
	("0385484518", "Tuesdays with Morrie: An Old Man, a Young Man, and Life's Greatest Lesson", 1, 2007, 13, 11.99),
	("B0015DROBO", "The Girl with a Dragon Tattoo", 1, "2008", 14, 9.99),
	("140882485X", "The Kite Runner", 1, 2004, 15, 11.99);

INSERT INTO Author (authorID, firstName, lastName) 
VALUES
	(1,"Gayle","Laakmann McDowell"),
	(2, "Cay", "Horstmann"),
	(3, "JK" ,"Rowling"),
	(4 , "George R. R." , "Martin"),
	(5 , "Mark" , "Lutz"),
	(6 , "Chris" , "Stewart"),
	(7, "J.R.R.", "Tolkien"),
	(8 , "Jon" , "Duckett"),
	(9, "Robert C.", "Martin"),
	(10, "Kent", "Beck"), 
	(11, "Michael", "Feathers"),
	(12, "Ramakrishna", "Reddy"),
	(13, "Mitch", "Albom"),
	(14, "Stieg", "Larsson"),
	(15, "Khaled", "Hosseini"); 

INSERT INTO AuthorISBN (authorID, isbn) 
VALUES
	(1, "0984782869"),
	(2, "1118431111"),
	(3, "0439064872"),
	(4, "0553593714"),
	(5, "1449355730"),
	(6, "0134706056"),
	(7, "054792819X"),
	(8 , "1118531647"),
	(9, "0132350882"),
	(10, "0201485672"),
	(11, "0131177052"),
	(12, "1974492559"),
	(13, "0385484518"),
	(14, "B0015DROBO"),
	(15, "140882485X"); 
	
INSERT INTO Publisher(publisherID, publisherName) 
VALUES
	(1, "CareerCup"),
	(2, "Wiley"),
	(3, "Scholastic"),
	(4, "Bantam"),
	(5 ,"O'Reilly Media"),
	(6, "Big Nerd Ranch Guides"),
	(7, "Mariner Books"),
	(8 , "Wiley"),
	(9 , "Prentice Hall"),
	(10, "Addison-Wesley Professional"),
	(11, "Prentice Hall"),
	(12, "PublicSpeakKing"),
	(13, "Broadway Books"),
	(14, "Vintage Crime"),
	(15, "Penguin Group");

