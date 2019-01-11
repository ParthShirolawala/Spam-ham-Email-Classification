import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Scanner;

public class NaiveBayesClassification
{
    public Set<String> vocabulory;
    public Map<String,Integer>spamtraindict;
    public Map<String,Integer>hamtraindict;
    public Map<String,Double>li_spam;
    public Map<String,Double>li_ham;
    public int spamwordtotalcount = 0;
    public int hamwordtotalcount = 0;

    public NaiveBayesClassification(TreeMap<String,Integer> hamtraindict, TreeMap<String,Integer> spamtraindict, Set<String> vocabulory)
    {
        this.hamtraindict = hamtraindict;
        this.spamtraindict = spamtraindict;
        this.vocabulory = vocabulory;
        li_ham  = new HashMap<>();
        li_spam = new HashMap<>();



    }
     public void train()
     {
         int spamwordcounter = 0;
         int hamwordcounter = 0;
         for(Entry<String,Integer> etr : spamtraindict.entrySet())
         {
             spamwordcounter+=etr.getValue();

         }

         for(Entry<String,Integer> etr : hamtraindict.entrySet())
         {
             hamwordcounter+=etr.getValue();
         }

         for(String element : vocabulory)
         {
             if(spamtraindict.containsKey(element))
             {
                 double logli_spam = Math.log(spamtraindict.get(element)+1.0)/(spamwordcounter + vocabulory.size()+1.0);
                 li_spam.put(element,logli_spam);
             }
         }

         for(String element : vocabulory)
         {
             if(hamtraindict.containsKey(element))
             {
                 double logli_ham = Math.log(hamtraindict.get(element)+1.0)/(hamwordcounter+vocabulory.size()+1.0);
                 li_ham.put(element,logli_ham);
             }
         }

         spamwordtotalcount = spamwordcounter;
         hamwordtotalcount = hamwordcounter;

     }

     public boolean test(File file, double logpriorhm, double logpriorsp, Set<String> stopwords, String filter)
     {
         double spamprob = 0.0;
         double hamprob = 0.0;
         Scanner sc = null;
         try {
             sc = new Scanner(file);
             while (sc.hasNext()) {
                 String line = sc.nextLine();
                 if (filter.equals("yes")) {
                     for (String element : line.trim().toLowerCase().split(" ")) {
                         if (!stopwords.contains(element)) {
                             if (li_spam.containsKey(element)) {
                                 spamprob += li_spam.get(element);
                             } else {
                                 spamprob += Math.log(1.0 / (spamwordtotalcount + vocabulory.size() + 1.0));

                             }
                             if (li_ham.containsKey(element)) {
                                 hamprob += li_ham.get(element);
                             } else {
                                 hamprob += Math.log(1.0 / (hamwordtotalcount + vocabulory.size() + 1.0));
                             }
                         }
                     }
                 } else {
                     for (String element : line.trim().toLowerCase().split(" "))
                     {
                         if (li_spam.containsKey(element))
                         {
                             spamprob += li_spam.get(element);
                         }
                         else
                         {
                             spamprob += Math.log(1.0 / (spamwordtotalcount + vocabulory.size() + 1.0));

                         }
                         if (li_ham.containsKey(element))
                         {
                             hamprob += li_ham.get(element);
                         }
                         else
                         {
                             hamprob += Math.log(1.0 / (hamwordtotalcount + vocabulory.size() + 1.0));
                         }
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

         spamprob += logpriorsp;
         hamprob += logpriorhm;

         if (spamprob > hamprob)
         {
             return false;
         }
         else
         {
             return true;
         }



     }
}

