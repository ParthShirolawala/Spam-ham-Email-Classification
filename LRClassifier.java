import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class LRClassifier
{
    public Set<String> vocabulory;
    public Set<String> hamset;
    public Set<String> spamset;
    public Set<String> totalset;
    public double eeta;
    public double lambda;
    public Map<String,Integer> spamtraindict;
    public Map<String,Integer> hamtraindict;
    public Map<String,HashMap<String,Integer>> spamdict;
    public Map<String,HashMap<String,Integer>> hamdict;

    public double w0 = 0.1;
    public static int constant = 35;
    public HashMap<String,Double>weightdict = new HashMap<String,Double>();

    public LRClassifier(Set<String> vocabulory, HashMap<String, HashMap<String,Integer>> hamdict, HashMap<String, HashMap<String,Integer>> spamdict,
                        Map<String,Integer> hamtraindict, Map<String,Integer> spamtraindict, double eeta, double lambda, Set<String> totalset, Set<String> spamset,
                        Set<String> hamset)
    {
        this.vocabulory = vocabulory;
        this.hamdict = hamdict;
        this.spamdict = spamdict;
        this.hamtraindict = hamtraindict;
        this.spamtraindict = spamtraindict;
        this.eeta = eeta;
        this.lambda = lambda;
        this.totalset = totalset;
        this.spamset = spamset;
        this.hamset = hamset;



    }
     public void train()
     {
         for(String word : vocabulory)
         {
             weightdict.put(word,0.5);
         }

         for(int i=0;i<constant;i++)
         {
             for(String word : vocabulory)
             {
                 double err =0;
                 for(String file : totalset)
                 {
                     int filetype;
                     int wordoccurrence = getOccurrence(file,word);
                     if(spamset.contains(file))
                     {
                         filetype = 0;
                     }
                     else
                     {
                         filetype = 1;
                     }

                     double predictedclass = analyze(file);
                     err += wordoccurrence * (filetype-predictedclass);
                 }
                 double revisedweight = weightdict.get(word) + eeta*err -(eeta*lambda*weightdict.get(word));
                 weightdict.put(word,revisedweight);

             }
         }


     }
     private double analyze(String file)
     {
        double pred = w0;
        if(hamset.contains(file))
        {
            for(Entry<String,Integer> trainworddict : hamdict.get(file).entrySet())
            {
                pred += weightdict.get(trainworddict.getKey()) * trainworddict.getValue();

            }
            return sig(pred);

        }
        else
        {
            for(Entry<String,Integer> trainwordict : spamdict.get(file).entrySet())
            {
                pred += weightdict.get(trainwordict.getKey()) * trainwordict.getValue();
            }
            return sig(pred);
        }

     }

     private double sig(double pred)
     {
         if(pred>290)
         {
             return 1.0;
         }
         else if(pred<-290)
         {
             return 0.0;

         }
         else
         {
             return(1.0/(1.0 + Math.exp(-pred)));
         }
     }

     private int getOccurrence(String file, String word)
     {
         if(hamset.contains(file))
         {
             for(Entry<String,Integer> entry : hamdict.get(file).entrySet())
             {
                 if(entry.getKey().equals(word))
                 {
                     return entry.getValue();
                 }
             }
         }
         else if(spamset.contains(file))
         {
             for(Entry<String,Integer> entry : spamdict.get(file).entrySet())
             {
                 if(entry.getKey().equals(word))
                 {
                     return entry.getValue();
                 }

             }
         }
         return 0;
     }

     public boolean test(HashMap<String,Integer> wordcount)
     {
         double pred = w0;
         for(Entry<String,Integer> etr : wordcount.entrySet())
         {
             if(weightdict.containsKey(etr.getKey()))
             {
                 pred += (weightdict.get(etr.getKey())*etr.getValue());

             }
         }
         if(pred>=0)
         {
             return true;

         }
         else
         {
             return false;
         }
     }
}