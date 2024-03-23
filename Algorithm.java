import java.util.*;


package model;

import java.security.Key;
import java.util.*;

    public class Algorithm {

        public void informationEntropy1(List<DocumentBase> searchDocuments, Map<String,List<String>> tags, String baseTag, List<String> eList, boolean lastTag, Map<String,Float> Ig)
        {
            String Key ="";
            Boolean loop=true;
            List<String> excludeList= new ArrayList<>();
            Map<String,List<String>> dcoList= new HashMap<>();

            for(Map.Entry<String,List<String>> entry:tags.entrySet())
            {
                dcoList.put(entry.getKey(),entry.getValue());
            }

            while(loop)
            {
                if ((Key=this.informationEntropy(searchDocuments, dcoList,baseTag,excludeList,lastTag,Ig))!="")
                {
                    //   processString(Key);
                    //   excludeList.add(Key);
                    dcoList.clear();
                    String[] keys = Key.split("/");
                    excludeList.add(keys[keys.length - 1]);
                    for (Map.Entry<String, List<String>> entry : tags.entrySet()) {
                        if (entry.getKey().equals(keys[keys.length - 1])) {
                            for (String tag : entry.getValue())
                                dcoList.put(Key + "=" + tag, null);
                            break;
                        }

                    }
                } else{
                    loop = false;
                    ;
                }


            }

        }



        List<String> excludeList=new ArrayList<>();
        String key ="";
        String strippedTag="";
        List<String> tagsforNextIteration = new ArrayList<>();
        ArrayList<Float> list = new ArrayList<>();
        int i=0;

        public String informationEntropy(List<DocumentBase> searchDocuments, Map<String,List<String>> tags,String baseTag,List<String> eList,boolean lastTag, Map<String,Float> Ig) {

            Ig.clear();
            Map<String,Float> Ig1 = new HashMap<>();

            for(Map.Entry<String,List<String>> entry:tags.entrySet())
            {
                Map<String,List<String>> tagsToSend=new HashMap<>();
                tagsToSend.put(entry.getKey(),entry.getValue());

                if(entry.getKey().indexOf('=')==-1) {
                    Ig1 = getInfoGain(getTagValueDocuments(entry.getKey(),searchDocuments),
                            getDocuments(getTagValueDocuments(entry.getKey(),searchDocuments),eList));
                    list.add(Ig1.get(entry.getKey()));
                    Ig.put(entry.getKey(),Ig1.get(entry.getKey()));
                }
                else {
                    //data presp
                    String [] Values = entry.getKey().split("/");
                    for(Map.Entry <String, Float> entry1:(getInfoGain(getTagValueDocuments(Values,searchDocuments),
                            getDocuments(getTagValueDocuments(Values,searchDocuments),eList)).entrySet())){
                        Ig1.put(entry.getKey()+"/"+entry1.getKey()+"=",entry1.getValue());;
                        Ig.put(entry.getKey()+"/"+entry1.getKey(),entry1.getValue());;
                        list.add(entry1.getValue());
                    }

                }




            }
            Collections.sort(list, new Comparator<Float>() {
                public int compare(Float flt, Float flt1) {
                    return (flt1).compareTo(flt);
                }
            });
            LinkedHashMap<String, Float> sortedMap = new LinkedHashMap<>();
            for (Float flt : list) {
                for (Map.Entry<String, Float> entry : Ig.entrySet()) {
                    if (entry.getValue().equals(flt)) {
                        sortedMap.put(entry.getKey(), flt);
                        if(flt>0.1){
                            System.out.println(baseTag + entry.getKey() + "=" + flt);}
                    }
                }
            }

            Map.Entry<String, Float> entry = sortedMap.entrySet().iterator().next();
            if(entry.getValue()!=0) {
                key = entry.getKey();
            }else {
                key="";
            }


            Ig = new HashMap<>();

            list.clear();

            return key;
            //getTagValueDocuments(String tagName, String tagValue, List<DocumentBase> documentListToSearch)

        }

        public Map<String, List<String>> getDocuments(List<DocumentBase> listOfDocuments,List<String> tagsToExclude) {
            Map<String, List<String>> docTags = new HashMap<>();
            for (int i = 0; i < listOfDocuments.size(); i++) {
                for (Map.Entry<String,String> tag : listOfDocuments.get(i).getTags().entrySet()) {
                    if(!tagsToExclude.contains(tag.getKey())) {
                        if(!docTags.containsKey(tag.getKey())) {
                            docTags.put(tag.getKey(), new ArrayList<>());
                        }
                        if(!docTags.get(tag.getKey()).contains(tag.getValue())) {
                            docTags.get(tag.getKey()).add(tag.getValue());
                        }
                    }
                }
            }
            return docTags;

        }

        public List<DocumentBase> getTagValueDocuments(String tagName, String tagValue, List<DocumentBase> documentListToSearch) {
            List<DocumentBase> tagValueDocuments = new ArrayList<>();
            for (DocumentBase tagDocument : documentListToSearch) {
                if (Objects.equals(tagDocument.getTags().get(tagName), tagValue)) {
                    tagValueDocuments.add(tagDocument);
                }

            }
            return tagValueDocuments;
        }

        public List<DocumentBase> getTagValueDocuments(String tagName, List<DocumentBase> documentListToSearch) {
            List<DocumentBase> tagValueDocuments = new ArrayList<>();
            for (DocumentBase tagDocument : documentListToSearch) {
                if (tagDocument.getTags().get(tagName) != null) {
                    tagValueDocuments.add(tagDocument);
                }

            }
            return tagValueDocuments;
        }
        public List<DocumentBase> getTagValueDocuments(String [] tagName, List<DocumentBase> documentListToSearch) {
            List<DocumentBase> tagValueDocuments = new ArrayList<>();
            tagValueDocuments.clear();
            List<DocumentBase> tagValueDocuments1 = new ArrayList<>();
            tagValueDocuments1.clear();
            tagValueDocuments1.addAll(documentListToSearch);
            for(int i=0;i<tagName.length;i++) {
                tagValueDocuments.clear();
                for (DocumentBase tagDocument : tagValueDocuments1) {
                    if(tagName[i].indexOf("=")!=-1) {
                        if (Objects.equals(tagDocument.getTags().get(tagName[i].split("=")[0]), tagName[i].split("=")[1])) {
                            tagValueDocuments.add(tagDocument);
                        }
                    }
                    else{
                        if (tagDocument.getTags().get(tagName[i]) != null) {
                            tagValueDocuments.add(tagDocument);
                        }
                    }

                }
                tagValueDocuments1.clear();
                tagValueDocuments1.addAll(tagValueDocuments);

            }
            return tagValueDocuments;
        }

        Map<String,Float> calculateIG(List<DocumentBase> searchDocuments, Map<String,List<String>> tags)
        {

            Map<String,Float> Ig = new HashMap<>();
            if (tags.isEmpty()) return Ig;

            Random rd = new Random();
            String test = (String) tags.keySet().toArray()[0];
            Ig.put(test,rd.nextFloat());


            return Ig;
        }

        public Map<String, Float> getInfoGain(List<DocumentBase> documentsToSearch, Map<String, List<String>> tags) {
            Map<String, Float> infoGains = new HashMap<>();
            for (Map.Entry<String,List<String>> tag : tags.entrySet()) {
                float remainingEntropy = 0;
                float initialEntropy = 0;
                for (int k = 0; k < tags.get(tag.getKey()).size(); k++) {
                    remainingEntropy += remainingEntropy(tag.getKey(), tags.get(tag.getKey()).get(k), getTagValueDocuments(tag.getKey(), tags.get(tag.getKey()).get(k), documentsToSearch), documentsToSearch);
                }
                initialEntropy = initialEntropy(documentsToSearch);
                infoGains.put(tag.getKey(),getInformationGain(initialEntropy,remainingEntropy));
            }
            return infoGains;

        }
        public float getInformationGain(float initialEntropy, float remainingEntropy) {
            return initialEntropy - remainingEntropy;
        }
        public float remainingEntropy(String tagName, String tagValue, List<DocumentBase> tagDocuments, List<DocumentBase> tagDocumentsToSearchIn) {
            float probablility = (getTotalAccessProb(getTagValueDocuments(tagName, tagValue, tagDocuments)) / getTotalAccessProb(tagDocumentsToSearchIn));
            return probablility * initialEntropy(getTagValueDocuments(tagName, tagValue, tagDocuments));
        }
        public double getCurrentProbLog2(DocumentBase currentDocument,List<DocumentBase> tagDocuments) {
            float currentTotalAccess = getTotalAccessProb(tagDocuments);
            double probLog2 = 0;
            float countProb = currentDocument.getAccessCount() / currentTotalAccess;
            probLog2 = countProb * (Math.log(countProb) / Math.log(2));
            return probLog2;
        }
        public float getTotalAccessProb(List<DocumentBase> tagDocuments) {
            float reqTotalAccess = 0;
            for (int i = 0; i < tagDocuments.size(); i++) {
                reqTotalAccess += tagDocuments.get(i).getAccessCount();
            }
            return reqTotalAccess;
        }
        public float  initialEntropy(List<DocumentBase> tagDocuments) {
            float totalAccess = getTotalAccessProb(tagDocuments);
            float entropy = 0;
            for (int i = 0; i < tagDocuments.size(); i++) {
                entropy += getCurrentProbLog2(tagDocuments.get(i),tagDocuments);
            }
            return (entropy * -1);
        }
    }


