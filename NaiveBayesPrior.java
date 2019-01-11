import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;
import java.util.Set;
import java.io.FileNotFoundException;

public class NaiveBayesPrior
{
    public Set<String> vocabulory;
    public Set<String> stopwords;
    public Map<String, Integer> spamtraindict;
    public Map<String, Integer> hamtraindict;


    public NaiveBayesPrior()
    {
        vocabulory = new HashSet<>();
        stopwords = new HashSet<>();
        spamtraindict = new TreeMap<>();
        hamtraindict = new TreeMap<>();


    }
    public static void main(String[] args) {
        String filter = "no";
        try {
            filter=args[0];

        } catch(Exception e) {


        }
        File spamtrainingfile = new File("train/spam");
        File hamtrainingfile = new File("train/ham");
        File spamtestingfile = new File("test/spam");
        File hamtestingfile = new File("test/ham");
        File stopwordfile = new File("stopwords.txt");

        NaiveBayesPrior pr = new NaiveBayesPrior();

        pr.addVocabulory(spamtrainingfile);
        pr.addVocabulory(hamtrainingfile);

        Scanner sc = null;

        try
        {
            sc = new Scanner(stopwordfile);
            while (sc.hasNext())
            {
                String stopword = sc.next();
                pr.stopwords.add(stopword);

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally {
            sc.close();
        }
        if (filter.toLowerCase().equals("yes")) {
            System.out.println("Removing stop words");
            for (String s : pr.stopwords) {
                s = s.trim().toLowerCase();
                if (pr.vocabulory.contains(s)) {
                    pr.vocabulory.remove(s);
                }
            }
        }

        pr.getVocabuloryCount(spamtrainingfile, (TreeMap<String, Integer>) pr.spamtraindict);
        pr.getVocabuloryCount(hamtrainingfile, (TreeMap<String, Integer>) pr.hamtraindict);

        NaiveBayesClassification nbc = new NaiveBayesClassification((TreeMap<String, Integer>) pr.hamtraindict, (TreeMap<String, Integer>) pr.spamtraindict, pr.vocabulory);
        nbc.train();

        double priorsp = spamtrainingfile.listFiles().length / (double) (spamtrainingfile.listFiles().length + hamtrainingfile.listFiles().length);
        double priorhm = 1 - priorsp;

        double logpriorsp = Math.log(priorsp);
        double logpriorhm = Math.log(priorhm);

        double accurate_spam = 0;
        int spfilecount = spamtestingfile.listFiles().length;
        for (File file : spamtestingfile.listFiles()) {
            if (nbc.test(file, logpriorhm, logpriorsp, pr.stopwords, filter) == false) {
                accurate_spam++;
            }
        }
        double spaccuracy = accurate_spam / (double) spfilecount;
        System.out.println("Spam Accuracy :" + spaccuracy * 100);


        double accurate_ham = 0;
        int hmfilecount = hamtestingfile.listFiles().length;
        for (File file: hamtestingfile.listFiles()) {
            if (nbc.test(file, logpriorhm, logpriorsp, pr.stopwords, filter) == true) {
                accurate_ham++;

            }
        }
        double hmaccuracy = accurate_ham / (double) hmfilecount;
        System.out.println("Ham Accuracy :" + hmaccuracy * 100);
    }
    private void getVocabuloryCount(File trainingfile, TreeMap<String,Integer> trainingdict)
    {
        for(File file : trainingfile.listFiles())
        {
            Scanner sc = null;
            try
            {
                sc = new Scanner(file);
                while(sc.hasNext())
                {
                    String line = sc.nextLine();
                    for(String word : line.toLowerCase().trim().split(""))
                    {
                        if(!word.isEmpty())
                        {
                            if(vocabulory.contains(word))
                            {
                                if(trainingdict.containsKey(word))
                                {
                                    trainingdict.put(word,trainingdict.get(word)+1);
                                }
                                else
                                {
                                        trainingdict.put(word,1);
                                }
                            }
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                sc.close();
            }
        }

    }

    public void addVocabulory(File spamtrainingfile)
    {
        for(File file: spamtrainingfile.listFiles())
        {
            Scanner sc = null;
            try
            {
                sc= new Scanner(file);
                while(sc.hasNext())
                {
                    String line = sc.nextLine();
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
            finally
            {
                sc.close();
            }
        }

    }





}
