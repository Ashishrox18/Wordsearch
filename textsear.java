public class MainActivity extends AppCompatActivity {
    EditText enteredWord;
    Button firstSuggestion;
    Button secondSuggestion;
    Button thirdSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enteredWord = findViewById(R.id.inputWord);
        firstSuggestion = findViewById(R.id.firstSuggestion);
        secondSuggestion = findViewById(R.id.secondSuggestion);
        thirdSuggestion = findViewById(R.id.thirdSuggestion);
        enteredWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String word = enteredWord.getText().toString();
                if (word.length() == 0){
                    return;
                }
                String [] closestWord = returnSuggestions(word, 3);
                if (closestWord != null){
                    firstSuggestion.setText(closestWord[0]);
                    secondSuggestion.setText(closestWord[1]);
                    thirdSuggestion.setText(closestWord[2]);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    int levenshtein(String token1, String token2) {
        int[] distances = new int[token1.length() + 1];

        for (int t1 = 1; t1 <= token1.length(); t1++) {
            if (token1.charAt(t1 - 1) == token2.charAt(0)) {
                distances[t1] = calcMin(distances[t1 - 1], t1 - 1, t1);
            } else {
                distances[t1] = calcMin(distances[t1 - 1], t1 - 1, t1) + 1;
            }
        }

        int dist = 0;
        for (int t2 = 1; t2 < token2.length(); t2++) {
            dist = t2 + 1;
            for (int t1 = 1; t1 <= token1.length(); t1++) {
                int tempDist;
                if (token1.charAt(t1 - 1) == token2.charAt(t2)) {
                    tempDist = calcMin(dist, distances[t1 - 1], distances[t1]);
                } else {
                    tempDist = calcMin(dist, distances[t1 - 1], distances[t1]) + 1;
                }
                distances[t1 - 1] = dist;
                dist = tempDist;
            }
            distances[token1.length()] = dist;
        }
        return dist;
    }

    static int calcMin(int a, int b, int c) {
        if (a <= b && a <= c) {
            return a;
        } else if (b <= a && b <= c) {
            return b;
        } else {
            return c;
        }
    }

    String [] returnSuggestions(String word, int numWords){
        String[] dictWordDist = new String[20000];
        BufferedReader reader;
        int wordIdx = 0;
        try {
            int wordDistance;
//            https://github.com/first20hours/google-10000-english
            reader = new BufferedReader(new InputStreamReader(getAssets().open("20k.txt")));
            String line = reader.readLine();
            while (line != null) {
                wordDistance = levenshtein(line.trim(), word);
                dictWordDist[wordIdx] = wordDistance + "-" + line.trim();
                line = reader.readLine();
                wordIdx++;
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Failed to read the file.");
            e.printStackTrace();
            return null;
        }
        Arrays.sort(dictWordDist);
        String[] closestWords = new String[numWords];
        String currWordDist;
        for (int i = 0; i < numWords; i++) {
            currWordDist = dictWordDist[i];
            String[] wordDetails = currWordDist.split("-");
            closestWords[i] = wordDetails[1];
            System.out.println(wordDetails[0] + " " + wordDetails[1]);
        }
        return closestWords;
    }

    public void selectWord(View view){
        Button button = (Button) view;
        enteredWord.setText(button.getText() + " ");
        enteredWord.setSelection(enteredWord.getText().length());
    }
}