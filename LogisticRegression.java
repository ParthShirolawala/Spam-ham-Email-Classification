import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Scanner;
import java.io.FileNotFoundException;

public class LogisticRegression
{
    public Set<String> vocabulory;
    public Set<String> stopwordset;
    public Map<String,Integer> spamtraindict;
    public Map<String,Integer> hamtraindict;
    public Set<String> spamset;
    public Set<String> hamset;
    public Set<String> totalset;
    public Map<String, HashMap<String,Integer>>spamdict;
    public Map<String, HashMap<String,Integer>>hamdict;

    public LogisticRegression()
    {
        vocabulory = new HashSet<>();
        stopwordset = new HashSet<>();
        spamtraindict = new HashMap<>();
        hamtraindict = new HashMap<>();
        spamset = new HashSet<>();
        hamset = new HashSet<>();
        totalset = new HashSet<>();
        spamdict = new HashMap<>();
        hamdict = new HashMap<>();

    }

    public static void main(String[] args)
    {
        String filter = "no";
        double eeta = 0.02;
        double lambda = 0.02;
        try {
            filter = args[0];
            eeta = Double.parseDouble(args[1]);
            lambda = Double.parseDouble(args[2]);
        }
        catch(Exception e)
        {

        }
        File spamtrainingfile = new File("train/spam");
        File hamtrainingfile = new File("train/ham");
        File spamtestingfile = new File("test/spam");
        File hamtestingfile = new File("test/ham");
        File stopwordfile = new File("stopwords.txt");

        LogisticRegression lr = new LogisticRegression();
        lr.addVocabulory(spamtrainingfile);
        lr.addVocabulory(hamtrainingfile);

        Scanner sc = null;
        try
        {
            sc = new Scanner(stopwordfile);
            while(sc.hasNext())
            {
                String wd = sc.next();
                lr.stopwordset.add(wd);
            }
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();

        }
        finally
        {
           sc.close();
        }

        if(filter.equals("yes"))
        {
            for(String s : lr.stopwordset)
            {
                if(lr.vocabulory.contains(s))
                {
                    lr.vocabulory.remove(s);
                }
            }
        }

        lr.getvocabulorycount(spamtrainingfile, (HashMap<String, HashMap<String,Integer>>) lr.spamdict, (HashMap<String,Integer>) lr.spamtraindict, (HashSet<String>) lr.spamset );
        lr.getvocabulorycount(hamtrainingfile, (HashMap<String, HashMap<String,Integer>>) lr.hamdict, (HashMap<String,Integer>) lr.hamtraindict, (HashSet<String>) lr.hamset );

        LRClassifier lrc = new LRClassifier(lr.vocabulory, (HashMap<String, HashMap<String,Integer>>) lr.hamdict, (HashMap<String, HashMap<String,Integer>>) lr.spamdict,
                lr.hamtraindict, lr.spamtraindict, eeta,lambda,lr.totalset,lr.spamset,lr.hamset);
        System.out.println("Training");
        System.out.println("It will take about 20-25 minutes for training");
        lrc.train();

        System.out.println("Testing Spam Files");
        int totalspam = spamtestingfile.listFiles().length;
        int predictedspam = 0;

        for(File tf : spamtestingfile.listFiles())
        {
            HashMap<String,Integer> wordcount = new HashMap<String,Integer>();
            try
            {



                Scanner sc1 = new Scanner(tf);
                while (sc1.hasNext()) {
                    String line = sc1.nextLine();
                    for (String word : line.trim().toLowerCase().split(" ")) {
                        if (wordcount.containsKey(word)) {
                            wordcount.put(word, wordcount.get(word) + 1);
                        } else {
                            wordcount.put(word, 1);
                        }
                    }
                }

            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();

            }


            if(filter.equals("yes"))
            {
                for(String stpwrd : lr.stopwordset)
                {
                    if(wordcount.containsKey(stpwrd))
                    {
                        wordcount.remove(stpwrd);
                    }
                }
            }

            boolean ham = lrc.test(wordcount);
            if(!ham)
            {
                predictedspam+=1;
            }
        }

        double accuracysp = ((double)predictedspam/(double)totalspam)*100;
        System.out.println("Accuracy of spam file is :"+(accuracysp));



        System.out.println("Testing ham Files");
        int totalham = hamtestingfile.listFiles().length;
        int predictedham = 0;
        for(File tf : hamtestingfile.listFiles())
        {
            HashMap<String,Integer> hamwordcount = new HashMap<String,Integer>();
            try
            {
             Scanner sc2 = new Scanner(tf);
             while(sc2.hasNext())
             {
                 String line = sc2.nextLine();
                 for(String wrd : line.trim().toLowerCase().split(" "))
                 {
                     if(hamwordcount.containsKey(wrd))
                     {
                         hamwordcount.put(wrd,hamwordcount.get(wrd)+1);

                     }
                     else
                     {
                         hamwordcount.put(wrd,1);
                     }
                 }
             }
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();

            }


            if(filter.equals("yes"))
            {
                for(String stpwrd : lr.stopwordset)
                {
                    if(hamwordcount.containsKey(stpwrd))
                    {
                        hamwordcount.remove(stpwrd);
                    }
                }
            }

            boolean ham = lrc.test(hamwordcount);
            if(ham)
            {
                predictedham+=1;
            }

        }
        double accuracyhm = ((double)predictedham/(double)totalham)*100;
        System.out.println("Accuracy of ham files:"+(accuracyhm));

        double accuracy = ((double)(predictedham+predictedspam)/(double)(totalham+totalspam))*100;
        System.out.println("Accuracy is "+(accuracy));




    }

    private void getvocabulorycount(File trainingfile, HashMap<String, HashMap<String,Integer>> filedict, HashMap<String,Integer> trainworddict, HashSet<String> fileset)
    {
        for(File file : trainingfile.listFiles())
        {
            Map<String,Integer> vocabmap = new HashMap<String,Integer>();
            fileset.add(file.getName());
            totalset.add(file.getName());
            try
            {
                Scanner sc3 = new Scanner(file);
                while(sc3.hasNext())
                {
                    String line = sc3.nextLine();
                    for(String word : line.trim().toLowerCase().split(" "))
                    {
                        if(!word.isEmpty())
                        {
                            if(vocabulory.contains(word))
                            {
                                if(trainworddict.containsKey(word))
                                {
                                    trainworddict.put(word,trainworddict.get(word)+1);
                                }
                                else
                                {
                                    trainworddict.put(word,1);

                                }
                                if(vocabmap.containsKey(word))
                                {
                                    vocabmap.put(word,vocabmap.get(word)+1);
                                }
                                else
                                {
                                    vocabmap.put(word,1);
                                }
                            }
                        }

                    }
                }

            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();

            }

            filedict.put(file.getName(),(HashMap<String,Integer>) vocabmap);

        }

    }

    private void addVocabulory(File spamtrainingfile)
    {
        for(File file:spamtrainingfile.listFiles())
        {
            try
            {
                Scanner sc4 = new Scanner(file);
                while(sc4.hasNext())
                {
                    String line = sc4.nextLine();
                    for(String word : line.trim().toLowerCase().split(" "))
                    {
                        if(!word.isEmpty())
                        {
                            vocabulory.add(word);
                        }
                    }
                }
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
            }

        }

    }

}