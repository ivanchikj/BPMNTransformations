static boolean areMutuallyExclusive (String condition1,
                                         String condition2) throws IOException {
        String appid = "U5P3RH-859Q7YWP47";

 //For some reason wolfram alpha says that an empty line is false.
        //Instead in our case, if I have no condition, I want to consider is as
        //Not mutually excluding anything.
        //So i have to hardcode that an empty string is always non-
        // mutually exclusive.
        if (condition1.length() == 0 || condition2.length() == 0){
            return false;
        }


        String c1 = condition1.toLowerCase().replaceAll("\"", "");
        String c2 = condition2.toLowerCase().replaceAll("\"", "");
        String answer = "";
        String question = c1 + " && " + c2;

        System.out.println("QUESTION: " +question);
        String url =
                "http://api.wolframalpha.com/v2/query?appid=" + appid + "&input"
                        + "=" + URLEncoder.encode(question, "UTF-8");
        //System.out.println(url);
        URL wolframAlpha = new URL(url);
        URLConnection q = wolframAlpha.openConnection();
        BufferedReader in =
                new BufferedReader(new InputStreamReader(q.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();

        //System.out.println(sb.toString());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document =
                    builder.parse(new InputSource(new StringReader(sb.toString())));
            NodeList pods = document.getElementsByTagName("pod");
            for (int i = 0 ; i < pods.getLength() ; i++) {
                Element pod = (Element) pods.item(i);
                if (pod.getAttribute("title").equals("Solutions")) {
                    Node plaintext =
                            pod.getElementsByTagName("plaintext").item(0);
                    System.out.println(plaintext.getTextContent());
                    answer = plaintext.getTextContent();
                } else if (pod.getAttribute("title").equals("Result")) {
                    Node plaintext =
                            pod.getElementsByTagName("plaintext").item(0);
                    System.out.println(plaintext.getTextContent());
                    answer = plaintext.getTextContent();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        answer = answer.toLowerCase();
        if (answer.contains("false") || answer.contains("exist")){
            //false it means that I made a false assertion.
            //WA would answer me "False"
            //If I asked an impossible equation (e.g. "a>0 && a <0")
            //then WA would answer me "No solutions exist"
            System.out.println("TRUEEEE");
            return true;
        }
        return false;
    }