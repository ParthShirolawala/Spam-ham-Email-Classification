# Spam-ham-Email-Classification
1) For running Naive Bayes in Cmd.

First change the path and lead it to where the java files are saved. Keep all spam and ham files in the same place where the java files are placed. Two folder should be there for train and test . Inside that other two folder of spam and ham for each of them. So the path for the files should look like train/spam.

Now once the path of the folder is set in cmd( for example : C:\Users\parth\NaiveBayesClassifier> )

1st step -  C:\Users\parth\NaiveBayesClassifier> javac NaiveBayesPrior.java

2nd step -  C:\Users\parth\NaiveBayesClassifier> java NaiveBayesPrior no  (if you want to remove the stopwords then use "yes" instead of "no" in the argument).


2) For running Logistic Regression in Cmd.

First Change the path and lead it to where all the java files are saved. Same file system as Naive Bayes will stay for Logistic Regression too.

For executing.
 1st Step - C:\Users\parth\NaiveBayesClassifier>javac LogisticRegression.java

 2nd Step - C:\Users\parth\NaiveBayesClassifier>java LogisticRegression no 0.01 0.01
		
		Note: This testing will take around 20-25 minutes.
		
		(It takes 3 input argument 1.Filter- remove the stopword or not. (Change to yes if you want to remove the stopwords)
					   2.Learning Rate. (Change to test for different values such as 0.05)
					   3.Regularization parameter lambda.(Change to test for different regularization parameter)).
