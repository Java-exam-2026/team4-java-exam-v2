-- 初期ユーザーデータ
-- パスワード: admin123 ($2a$10$ZVp3xzVThoTkrKsPUEe.V.A7HPduCd9Y9laAkxNpnHKJ91dPJ/daK)
-- パスワード: user123 ($2a$10$cP7gGuoXml/EyL6mdEaBou92Hz2ScD32YVXdH/A41EZPfKqLqH9Mi)
INSERT INTO users (id, username, password, display_name, role) VALUES 
('550e8400-e29b-41d4-a716-446655440001', 'admin', '$2a$10$ZVp3xzVThoTkrKsPUEe.V.A7HPduCd9Y9laAkxNpnHKJ91dPJ/daK', '管理者', 'ROLE_ADMIN'),
('550e8400-e29b-41d4-a716-446655440002', 'testuser', '$2a$10$cP7gGuoXml/EyL6mdEaBou92Hz2ScD32YVXdH/A41EZPfKqLqH9Mi', 'テストユーザー', 'ROLE_USER')
ON CONFLICT (id) DO UPDATE SET 
    password = EXCLUDED.password,
    display_name = EXCLUDED.display_name,
    role = EXCLUDED.role;

-- 章データ
INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
('660e8400-e29b-41d4-a716-446655440013', 'chap3', '3章:値と計算', 3),
('660e8400-e29b-41d4-a716-446655440014', 'chap4', '4章:変数と型', 4),
('660e8400-e29b-41d4-a716-446655440015', 'chap5', '5章:標準API', 5),
('660e8400-e29b-41d4-a716-446655440017', 'chap7', '7章:クラスとオブジェクト', 7),
('660e8400-e29b-41d4-a716-446655440018', 'chap8', '8章:継承と多態性', 8),
('660e8400-e29b-41d4-a716-446655440019', 'chap9', '9章:抽象化', 9),
('660e8400-e29b-41d4-a716-446655440020', 'chap10', '10章:例外処理', 10),
('660e8400-e29b-41d4-a716-446655440021', 'chap11', '11章:コレクション', 11),
('660e8400-e29b-41d4-a716-446655440022', 'chap12', '12章:高度なトピック', 12)
ON CONFLICT (id) DO NOTHING;


-- Chapter 3: Values and Calculations
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('412fd8ac-093f-4cc6-a1a5-ee7c2e821602', '660e8400-e29b-41d4-a716-446655440013', 'Javaにおける 10 / 3 の結果は？', '{"A": "3.333", "B": "3", "C": "3.0", "D": "10"}', 'B', 'SINGLE_CHOICE'),
('fe6cff01-1578-4d08-8a7a-5db9de4296ac', '660e8400-e29b-41d4-a716-446655440013', '10 % 3 の結果は？', '{"A": "1", "B": "3", "C": "0", "D": "3.33"}', 'A', 'SINGLE_CHOICE'),
('5e9e8b80-fd4f-48c0-8185-e796e7342f2c', '660e8400-e29b-41d4-a716-446655440013', '5 + 2 * 3 の結果は？', '{"A": "21", "B": "10", "C": "11", "D": "13"}', 'C', 'SINGLE_CHOICE'),
('5466dede-112c-4486-b968-ca7adf9b4b9e', '660e8400-e29b-41d4-a716-446655440013', '"Java" + 1 + 2 の結果は？', '{"A": "Java12", "B": "Java3", "C": "Error", "D": "Java 3"}', 'A', 'SINGLE_CHOICE'),
('0d8aeabe-2d87-4d2f-8a29-09d327f068ae', '660e8400-e29b-41d4-a716-446655440013', '1 + 2 + "Java" の結果は？', '{"A": "12Java", "B": "3Java", "C": "Error", "D": "1 2 Java"}', 'B', 'SINGLE_CHOICE'),
('52e1e4c9-14f3-4d20-a8c6-034c568a92fb', '660e8400-e29b-41d4-a716-446655440013', 'double d = 10 / 4; の実行後の d の値は？', '{"A": "2.0", "B": "2.5", "C": "2", "D": "Error"}', 'A', 'SINGLE_CHOICE'),
('c99fbccd-2ad8-454a-9236-b116528f1edf', '660e8400-e29b-41d4-a716-446655440013', 'double d = 10.0 / 4; の実行後の d の値は？', '{"A": "2.0", "B": "2.5", "C": "2", "D": "Error"}', 'B', 'SINGLE_CHOICE'),
('e6bb5f0f-7c84-4837-9104-1aa084df2cb8', '660e8400-e29b-41d4-a716-446655440013', 'int x = 5; x++; の実行後の x の値は？', '{"A": "5", "B": "6", "C": "4", "D": "Error"}', 'B', 'SINGLE_CHOICE'),
('b2d1e436-320a-4832-9f84-56691cbec6c1', '660e8400-e29b-41d4-a716-446655440013', '!true の結果は？', '{"A": "false", "B": "true", "C": "0", "D": "1"}', 'A', 'SINGLE_CHOICE'),
('52084b0e-a244-4980-ba21-d38eb425b5bb', '660e8400-e29b-41d4-a716-446655440013', '等価比較に使用される演算子は？', '{"A": "=", "B": "==", "C": "===", "D": "<>"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 4: Variables and Types
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('2fa0d90f-f557-4a5d-96e6-96683b858c46', '660e8400-e29b-41d4-a716-446655440014', 'Javaにおいてint型は何ビットですか？', '{"A": "16", "B": "32", "C": "64", "D": "8"}', 'B', 'SINGLE_CHOICE'),
('ef0712c5-dab8-4ffd-8bcb-c4b38948570f', '660e8400-e29b-41d4-a716-446655440014', 'long型のリテラルに使用される接尾辞は？', '{"A": "I", "B": "D", "C": "L", "D": "F"}', 'C', 'SINGLE_CHOICE'),
('ec7aa710-1217-48db-a2c8-d6f9fe26cb8b', '660e8400-e29b-41d4-a716-446655440014', 'float型のリテラルに使用される接尾辞は？', '{"A": "F", "B": "D", "C": "L", "D": "R"}', 'A', 'SINGLE_CHOICE'),
('d779b1fa-0ca0-4f56-b3dd-03255e39b0dc', '660e8400-e29b-41d4-a716-446655440014', 'Javaにおいてchar型のサイズは？', '{"A": "8 bits", "B": "16 bits", "C": "32 bits", "D": "64 bits"}', 'B', 'SINGLE_CHOICE'),
('2f0c11b7-c5a1-4445-b0f1-f09c31da46b3', '660e8400-e29b-41d4-a716-446655440014', 'boolean型の有効な値は？', '{"A": "true, false", "B": "0, 1", "C": "yes, no", "D": "TRUE, FALSE"}', 'A', 'SINGLE_CHOICE'),
('1c2f6090-c53c-4891-822e-6f529ab5e734', '660e8400-e29b-41d4-a716-446655440014', 'int型のインスタンス変数のデフォルト値は？', '{"A": "null", "B": "undefined", "C": "0", "D": "1"}', 'C', 'SINGLE_CHOICE'),
('e88ee90a-6f39-41ca-8959-dc99b6073636', '660e8400-e29b-41d4-a716-446655440014', 'boolean型のインスタンス変数のデフォルト値は？', '{"A": "true", "B": "false", "C": "null", "D": "0"}', 'B', 'SINGLE_CHOICE'),
('a0aff64f-756d-4ed4-935c-2f6c518d7868', '660e8400-e29b-41d4-a716-446655440014', '無効な変数名はどれですか？', '{"A": "myVar", "B": "_myVar", "C": "$myVar", "D": "1stVar"}', 'D', 'SINGLE_CHOICE'),
('69949542-785a-439f-8db8-5e0da2bc78e4', '660e8400-e29b-41d4-a716-446655440014', 'double型をint型にキャストする方法は？', '{"A": "(int) d", "B": "int(d)", "C": "d.toInt()", "D": "cast(d, int)"}', 'A', 'SINGLE_CHOICE'),
('2f57472c-8121-4411-809f-76b027d57684', '660e8400-e29b-41d4-a716-446655440014', 'Stringはプリミティブ型ですか？', '{"A": "Yes", "B": "No", "C": "Sometimes", "D": "Depends on JVM"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 5: Standard API
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('d6bd5eef-29fa-4078-a286-cccdce640a90', '660e8400-e29b-41d4-a716-446655440015', 'Stringの長さを返すメソッドは？', '{"A": "length()", "B": "size()", "C": "getSize()", "D": "count()"}', 'A', 'SINGLE_CHOICE'),
('f08bfa3b-cd1c-443f-b342-6c275adbfec0', '660e8400-e29b-41d4-a716-446655440015', '指定したインデックスの文字を返すメソッドは？', '{"A": "getChar()", "B": "charAt()", "C": "char()", "D": "index()"}', 'B', 'SINGLE_CHOICE'),
('1c012e9d-7d75-49ef-98ee-164c19717a25', '660e8400-e29b-41d4-a716-446655440015', '"Hello".substring(0, 2) の結果は？', '{"A": "He", "B": "Hel", "C": "el", "D": "lo"}', 'A', 'SINGLE_CHOICE'),
('53cb0d98-64cd-43b9-82e4-4dfa415a90a5', '660e8400-e29b-41d4-a716-446655440015', '2つのStringの内容が等しいか比較する方法は？', '{"A": "equals()", "B": "==", "C": "=", "D": "compare()"}', 'A', 'SINGLE_CHOICE'),
('8c9d9184-3ddd-435d-8fc7-397d7a8c30d1', '660e8400-e29b-41d4-a716-446655440015', 'Math.max(10, 20) の結果は？', '{"A": "10", "B": "20", "C": "30", "D": "0"}', 'B', 'SINGLE_CHOICE'),
('81e0c85f-bd38-4639-83bf-771dc5228c7c', '660e8400-e29b-41d4-a716-446655440015', 'Math.min(10, 20) の結果は？', '{"A": "10", "B": "20", "C": "30", "D": "0"}', 'A', 'SINGLE_CHOICE'),
('26b56144-2f82-4a0b-8ece-183c50b79115', '660e8400-e29b-41d4-a716-446655440015', 'Math.abs(-5) の結果は？', '{"A": "-5", "B": "0", "C": "5", "D": "Error"}', 'C', 'SINGLE_CHOICE'),
('c4577ba6-ea61-442b-be3a-a503d2b8dbbe', '660e8400-e29b-41d4-a716-446655440015', 'コンソールにテキストを出力するメソッドは？', '{"A": "System.out.println()", "B": "Console.log()", "C": "print()", "D": "echo()"}', 'A', 'SINGLE_CHOICE'),
('3106f3ef-ec73-4cb9-8ea9-4a9a55387a46', '660e8400-e29b-41d4-a716-446655440015', '整数を読み取るScannerのメソッドは？', '{"A": "readInt()", "B": "nextInt()", "C": "getInteger()", "D": "inputInt()"}', 'B', 'SINGLE_CHOICE'),
('09ae29f7-d05b-453c-9ee5-b51f17e57ab5', '660e8400-e29b-41d4-a716-446655440015', 'Random.nextInt(10) の範囲は？', '{"A": "1 to 10", "B": "0 to 10", "C": "0 to 9", "D": "1 to 9"}', 'C', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 7: Classes and Objects
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('5bc0953b-411d-4cef-b75e-2779e0db8d6a', '660e8400-e29b-41d4-a716-446655440017', 'Javaにおけるクラスの説明として最も適切なものはどれですか？', '{"A": "オブジェクトの設計図", "B": "メモリ上の実体", "C": "基本データ型の一つ", "D": "メソッドの集合体"}', 'A', 'SINGLE_CHOICE'),
('3d928fa3-cf7a-46bb-9241-af1adcb23237', '660e8400-e29b-41d4-a716-446655440017', '新しいオブジェクトを生成するために使用するキーワードはどれですか？', '{"A": "create", "B": "make", "C": "new", "D": "instance"}', 'C', 'SINGLE_CHOICE'),
('ffe6befd-80ee-468f-b0a9-f746bab159ca', '660e8400-e29b-41d4-a716-446655440017', 'コンストラクタの特徴として正しいものはどれですか？', '{"A": "戻り値の型はvoidである", "B": "クラス名と同じ名前を持つ", "C": "必ず引数が必要である", "D": "static修飾子が必須である"}', 'B', 'SINGLE_CHOICE'),
('78bc1d45-1c5c-4ef3-a53d-0ab3bdac33d5', '660e8400-e29b-41d4-a716-446655440017', 'キーワード `this` は何を参照しますか？', '{"A": "親クラスのインスタンス", "B": "現在のクラスの静的メンバ", "C": "現在のオブジェクト自身", "D": "メインメソッド"}', 'C', 'SINGLE_CHOICE'),
('79f8ecb1-e8bb-410d-be75-f2aaf9db58a8', '660e8400-e29b-41d4-a716-446655440017', 'int型のインスタンス変数のデフォルト値はどれですか？', '{"A": "null", "B": "0", "C": "1", "D": "undefined"}', 'B', 'SINGLE_CHOICE'),
('d797ba89-67bb-40ed-9c92-22436ac6c38b', '660e8400-e29b-41d4-a716-446655440017', '1つのクラスに複数のコンストラクタを定義することを何と呼びますか？', '{"A": "オーバーライド", "B": "オーバーロード", "C": "カプセル化", "D": "継承"}', 'B', 'SINGLE_CHOICE'),
('2afe315e-e1df-4233-b82e-3f8a0a896153', '660e8400-e29b-41d4-a716-446655440017', 'インスタンス変数の説明として正しいものはどれですか？', '{"A": "メソッド内で宣言される変数", "B": "クラス全体で共有される変数", "C": "オブジェクトごとに独立した値を持つ変数", "D": "定数として扱われる変数"}', 'C', 'SINGLE_CHOICE'),
('3432bc6e-98cc-40a5-970f-247802699cb4', '660e8400-e29b-41d4-a716-446655440017', 'コンストラクタに戻り値の型を指定した場合どうなりますか？', '{"A": "コンパイルエラーになる", "B": "通常のメソッドとして扱われる", "C": "警告が出るが動作する", "D": "実行時エラーになる"}', 'B', 'SINGLE_CHOICE'),
('534c3980-6022-4667-b5b1-e1ca080693ee', '660e8400-e29b-41d4-a716-446655440017', 'staticメソッドからインスタンス変数に直接アクセスできますか？', '{"A": "できる", "B": "できない", "C": "publicならできる", "D": "privateならできる"}', 'B', 'SINGLE_CHOICE'),
('0bf0fa1a-4cbd-4952-83e5-68df719250f8', '660e8400-e29b-41d4-a716-446655440017', '不要になったオブジェクトのメモリを解放する仕組みはどれですか？', '{"A": "デストラクタ", "B": "ガベージコレクション", "C": "メモリクリーナー", "D": "ヒープクリア"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;

-- Chapter 8: Inheritance and Polymorphism
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('fb94055b-09e4-440e-90d5-1213829b8561', '660e8400-e29b-41d4-a716-446655440018', 'クラスを継承する際に使用するキーワードはどれですか？', '{"A": "implements", "B": "inherits", "C": "extends", "D": "super"}', 'C', 'SINGLE_CHOICE'),
('db39b77f-ddfb-428a-8c94-dbd70115dafa', '660e8400-e29b-41d4-a716-446655440018', '親クラスのメソッドをサブクラスで再定義することを何と呼びますか？', '{"A": "オーバーロード", "B": "オーバーライド", "C": "抽象化", "D": "カプセル化"}', 'B', 'SINGLE_CHOICE'),
('09ba0d13-4f64-41a9-8066-532a54ef3867', '660e8400-e29b-41d4-a716-446655440018', 'キーワード `super` の用途として正しいものはどれですか？', '{"A": "現在のオブジェクトを参照する", "B": "親クラスのコンストラクタやメンバを呼び出す", "C": "静的メソッドを呼び出す", "D": "定数を定義する"}', 'B', 'SINGLE_CHOICE'),
('53362c8d-0a05-4373-a306-45d929235538', '660e8400-e29b-41d4-a716-446655440018', 'Javaにおいて、1つのクラスが継承できるクラスの数は？', '{"A": "1つのみ", "B": "2つまで", "C": "無制限", "D": "継承はできない"}', 'A', 'SINGLE_CHOICE'),
('5ed10860-b157-4f57-92b6-70c02984c560', '660e8400-e29b-41d4-a716-446655440018', '全てのJavaクラスの親となるルートクラスはどれですか？', '{"A": "Main", "B": "System", "C": "Class", "D": "Object"}', 'D', 'SINGLE_CHOICE'),
('772b59aa-4754-4f3b-bbbf-3308326495d1', '660e8400-e29b-41d4-a716-446655440018', 'ポリモーフィズム（多態性）の説明として最も適切なものはどれですか？', '{"A": "データを隠蔽すること", "B": "同じ操作で異なる振る舞いをすること", "C": "コードを再利用すること", "D": "複数のクラスを継承すること"}', 'B', 'SINGLE_CHOICE'),
('cad39775-db7d-46df-8d4a-67ca5d528bab', '660e8400-e29b-41d4-a716-446655440018', 'どこからでもアクセス可能なアクセス修飾子はどれですか？', '{"A": "private", "B": "protected", "C": "public", "D": "default"}', 'C', 'SINGLE_CHOICE'),
('4c549c36-c468-4c74-a411-e7c6f7ba4007', '660e8400-e29b-41d4-a716-446655440018', '同じパッケージ内およびサブクラスからアクセス可能な修飾子はどれですか？', '{"A": "private", "B": "protected", "C": "public", "D": "なし（package-private）"}', 'B', 'SINGLE_CHOICE'),
('74b3767c-7982-42ca-99ed-a34e2c8fcdfd', '660e8400-e29b-41d4-a716-446655440018', 'final修飾子がついたメソッドの特徴はどれですか？', '{"A": "オーバーライドできない", "B": "呼び出すことができない", "C": "戻り値がない", "D": "必ずオーバーライドしなければならない"}', 'A', 'SINGLE_CHOICE'),
('eb5739ff-080a-4e41-b612-94f7195a5b02', '660e8400-e29b-41d4-a716-446655440018', '実行時にオブジェクトの型に基づいてメソッドが選択される仕組みを何と呼びますか？', '{"A": "静的バインディング", "B": "動的バインディング", "C": "キャスト", "D": "コンパイル"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;

-- Chapter 9: Abstraction
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('697e0a6e-9027-425b-9d44-d4c6b3f5ba73', '660e8400-e29b-41d4-a716-446655440019', '抽象クラスを定義するためのキーワードはどれですか？', '{"A": "virtual", "B": "interface", "C": "abstract", "D": "static"}', 'C', 'SINGLE_CHOICE'),
('fc5bcee7-3fdd-4976-b68d-f68a00da2ebe', '660e8400-e29b-41d4-a716-446655440019', '抽象クラスについて正しい説明はどれですか？', '{"A": "newキーワードでインスタンス化できる", "B": "抽象メソッドを持つことができない", "C": "インスタンス化できない", "D": "全てのメソッドが抽象メソッドでなければならない"}', 'C', 'SINGLE_CHOICE'),
('b090837b-ae86-4599-9e76-aa7bc0810fbf', '660e8400-e29b-41d4-a716-446655440019', 'インターフェースを定義するためのキーワードはどれですか？', '{"A": "class", "B": "interface", "C": "implements", "D": "abstract"}', 'B', 'SINGLE_CHOICE'),
('988e8d04-e0c7-47ed-9315-f427c1266455', '660e8400-e29b-41d4-a716-446655440019', 'クラスがインターフェースを実装する際に使用するキーワードはどれですか？', '{"A": "extends", "B": "uses", "C": "implements", "D": "imports"}', 'C', 'SINGLE_CHOICE'),
('7735eaa8-e76d-435d-a3da-f464b3d7366f', '660e8400-e29b-41d4-a716-446655440019', 'インターフェース内で宣言されたフィールドのデフォルトの修飾子はどれですか？', '{"A": "private", "B": "protected", "C": "public static final", "D": "public abstract"}', 'C', 'SINGLE_CHOICE'),
('2c55d5c7-80dd-4473-bff8-9e46f1489b63', '660e8400-e29b-41d4-a716-446655440019', '抽象メソッドの特徴として正しいものはどれですか？', '{"A": "処理内容（ボディ）を持たない", "B": "必ずprivateである", "C": "staticメソッドにできる", "D": "final修飾子をつけられる"}', 'A', 'SINGLE_CHOICE'),
('417153b9-23ed-461c-a552-5a03255dee30', '660e8400-e29b-41d4-a716-446655440019', 'Javaで多重継承のような機能を実現するために使用されるものはどれですか？', '{"A": "抽象クラス", "B": "インターフェース", "C": "内部クラス", "D": "パッケージ"}', 'B', 'SINGLE_CHOICE'),
('b29535e4-7da3-4678-a589-e2e8ef7882f4', '660e8400-e29b-41d4-a716-446655440019', 'Java 8以降でインターフェースに実装を持たせることができるメソッドはどれですか？', '{"A": "abstractメソッド", "B": "defaultメソッド", "C": "nativeメソッド", "D": "finalメソッド"}', 'B', 'SINGLE_CHOICE'),
('52a95f7f-babb-415e-be12-21e18d3c89ad', '660e8400-e29b-41d4-a716-446655440019', '抽象クラスはコンストラクタを持つことができますか？', '{"A": "はい", "B": "いいえ", "C": "引数なしのみ可", "D": "privateのみ可"}', 'A', 'SINGLE_CHOICE'),
('e2abfe1d-98d4-4dd2-9b5e-b3444676dfae', '660e8400-e29b-41d4-a716-446655440019', 'インターフェースと抽象クラスの主な違いとして正しいものはどれですか？', '{"A": "インターフェースは多重実装が可能だが、抽象クラスは単一継承のみ", "B": "抽象クラスはメソッドを持てない", "C": "インターフェースはインスタンス化できる", "D": "違いはない"}', 'A', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;

-- Chapter 10: Exceptions
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('f4bec053-93e2-4a2d-b909-f1169f01343d', '660e8400-e29b-41d4-a716-446655440020', '例外処理において、例外の発生有無に関わらず必ず実行されるブロックはどれですか？', '{"A": "try", "B": "catch", "C": "finally", "D": "throw"}', 'C', 'SINGLE_CHOICE'),
('3c968c53-3d07-4f18-bb75-02f29442ca3a', '660e8400-e29b-41d4-a716-446655440020', 'メソッドが例外を投げる可能性があることを宣言するために使用するキーワードはどれですか？', '{"A": "throw", "B": "throws", "C": "try", "D": "catch"}', 'B', 'SINGLE_CHOICE'),
('da282152-2b2d-49ef-9680-cbd63970231b', '660e8400-e29b-41d4-a716-446655440020', 'プログラム内で意図的に例外を発生させるために使用するキーワードはどれですか？', '{"A": "throws", "B": "throw", "C": "new", "D": "raise"}', 'B', 'SINGLE_CHOICE'),
('bd8e6f54-115d-4f6d-9eb9-b4f9999c5bf0', '660e8400-e29b-41d4-a716-446655440020', '次のうち、非チェック例外（Unchecked Exception）はどれですか？', '{"A": "IOException", "B": "SQLException", "C": "NullPointerException", "D": "ClassNotFoundException"}', 'C', 'SINGLE_CHOICE'),
('007a2e66-9100-482d-b350-0613d0c64db1', '660e8400-e29b-41d4-a716-446655440020', 'try-with-resources文を使用するために、リソースクラスが実装する必要があるインターフェースはどれですか？', '{"A": "Closeable", "B": "AutoCloseable", "C": "Disposable", "D": "Readable"}', 'B', 'SINGLE_CHOICE'),
('cabe8751-64e4-4fec-b3b9-439c096425ea', '660e8400-e29b-41d4-a716-446655440020', '複数のcatchブロックを記述する場合の正しい順序はどれですか？', '{"A": "スーパークラスからサブクラスへ", "B": "サブクラスからスーパークラスへ", "C": "順序は関係ない", "D": "ランダム"}', 'B', 'SINGLE_CHOICE'),
('686fdab2-a3a4-4272-bcfb-98246f719886', '660e8400-e29b-41d4-a716-446655440020', 'すべての例外とエラーの親クラスはどれですか？', '{"A": "Exception", "B": "Error", "C": "Throwable", "D": "Object"}', 'C', 'SINGLE_CHOICE'),
('4edb2834-5d1c-46ec-a24f-51c29676cbf8', '660e8400-e29b-41d4-a716-446655440020', '独自の例外クラスを作成する場合、通常継承するクラスはどれですか？', '{"A": "String", "B": "System", "C": "Exception", "D": "Error"}', 'C', 'SINGLE_CHOICE'),
('80e21d8c-3e94-42ee-b8c0-3796f3885f7c', '660e8400-e29b-41d4-a716-446655440020', '例外のエラーメッセージを取得するメソッドはどれですか？', '{"A": "toString()", "B": "getMessage()", "C": "printStackTrace()", "D": "getError()"}', 'B', 'SINGLE_CHOICE'),
('724b46cb-ab0a-4705-bd28-4b490eca84ba', '660e8400-e29b-41d4-a716-446655440020', '次のコードでコンパイルエラーになる原因は？ try { ... } catch (Exception e) { ... } catch (IOException e) { ... }', '{"A": "tryブロックが空だから", "B": "IOExceptionはExceptionのサブクラスであり、到達不能コードになるから", "C": "catchブロックが多すぎるから", "D": "finallyブロックがないから"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;

-- Chapter 11: Collections
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('cd851b03-a832-4a41-a50e-2124b0138739', '660e8400-e29b-41d4-a716-446655440021', '重複する要素を許可しないコレクションインターフェースはどれですか？', '{"A": "List", "B": "Set", "C": "Map", "D": "Queue"}', 'B', 'SINGLE_CHOICE'),
('c301d02f-1799-48b4-adc1-56f6d0754d42', '660e8400-e29b-41d4-a716-446655440021', 'キーと値のペアを管理するインターフェースはどれですか？', '{"A": "Collection", "B": "List", "C": "Set", "D": "Map"}', 'D', 'SINGLE_CHOICE'),
('c249175c-438e-4c6a-a325-8fc4d5a19b1a', '660e8400-e29b-41d4-a716-446655440021', '要素の挿入順序を保持し、重複を許可するコレクションはどれですか？', '{"A": "HashSet", "B": "TreeSet", "C": "ArrayList", "D": "HashMap"}', 'C', 'SINGLE_CHOICE'),
('fd090047-2391-45c4-942f-9a2dbc666c60', '660e8400-e29b-41d4-a716-446655440021', 'LIFO（後入れ先出し）のデータ構造を表すクラスはどれですか？', '{"A": "Queue", "B": "Stack", "C": "List", "D": "Vector"}', 'B', 'SINGLE_CHOICE'),
('a74dd47f-952f-4158-9d68-6d40f52adb2f', '660e8400-e29b-41d4-a716-446655440021', 'ジェネリクスを使用してString型のリストを作成する正しい記述はどれですか？', '{"A": "List list = new List();", "B": "List<String> list = new ArrayList<>();", "C": "ArrayList<String> list = new List<>();", "D": "List(String) list = new ArrayList();"}', 'B', 'SINGLE_CHOICE'),
('cb8bcfac-e328-4c7c-a589-fa0c9279728b', '660e8400-e29b-41d4-a716-446655440021', 'Mapに含まれるすべてのキーを取得するメソッドはどれですか？', '{"A": "getValues()", "B": "entrySet()", "C": "keySet()", "D": "keyList()"}', 'C', 'SINGLE_CHOICE'),
('8b38cd3e-15c0-4531-9505-3cf6556a4b48', '660e8400-e29b-41d4-a716-446655440021', 'コレクション内の要素を順番に走査するためのインターフェースはどれですか？', '{"A": "Scanner", "B": "Iterator", "C": "Enumerator", "D": "Traverser"}', 'B', 'SINGLE_CHOICE'),
('b0415933-b635-4a09-9086-145ec934b312', '660e8400-e29b-41d4-a716-446655440021', 'ArrayListとLinkedListの違いについて正しい記述はどれですか？', '{"A": "LinkedListはランダムアクセスが高速", "B": "ArrayListは要素の挿入・削除が高速", "C": "ArrayListはランダムアクセスが高速", "D": "両者に性能差はない"}', 'C', 'SINGLE_CHOICE'),
('d7745712-94e8-4a2b-92f7-8ebced991430', '660e8400-e29b-41d4-a716-446655440021', 'Collections.sort()でリストをソートするために、要素が実装すべきインターフェースはどれですか？', '{"A": "Cloneable", "B": "Serializable", "C": "Comparable", "D": "Runnable"}', 'C', 'SINGLE_CHOICE'),
('d3db2a42-9d2c-4d71-92de-bbc5831f0bd6', '660e8400-e29b-41d4-a716-446655440021', 'Java 7以降で導入された、ジェネリクスの型推論を行う演算子 <> の名称は？', '{"A": "アロー演算子", "B": "ダイヤモンド演算子", "C": "ドット演算子", "D": "エルビス演算子"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;

-- Chapter 12: Advanced Topics
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('7ab65eff-fbf3-4892-92cb-eeb9dd3de8ca', '660e8400-e29b-41d4-a716-446655440022', 'ラムダ式の基本的な構文として正しいものはどれですか？', '{"A": "(引数) -> { 処理 }", "B": "{ 処理 } -> (引数)", "C": "function(引数) { 処理 }", "D": "[引数] => { 処理 }"}', 'A', 'SINGLE_CHOICE'),
('c10f9532-90b5-4ecf-a108-d1e70f04a561', '660e8400-e29b-41d4-a716-446655440022', '抽象メソッドを1つだけ持つインターフェースを何と呼びますか？', '{"A": "抽象インターフェース", "B": "関数型インターフェース", "C": "単一インターフェース", "D": "ラムダインターフェース"}', 'B', 'SINGLE_CHOICE'),
('6dc76b3c-ed24-4e21-a0b9-3cb9fed45256', '660e8400-e29b-41d4-a716-446655440022', 'Stream APIにおいて、条件に一致する要素のみを抽出する中間操作はどれですか？', '{"A": "map", "B": "reduce", "C": "filter", "D": "collect"}', 'C', 'SINGLE_CHOICE'),
('566b587d-85ae-44ec-a29f-ea94c8b0c60f', '660e8400-e29b-41d4-a716-446655440022', 'Stream APIにおいて、要素を別の形式に変換する中間操作はどれですか？', '{"A": "filter", "B": "map", "C": "forEach", "D": "sorted"}', 'B', 'SINGLE_CHOICE'),
('b0fd0779-cadb-40aa-96db-e8bf9476096d', '660e8400-e29b-41d4-a716-446655440022', '値が存在しない可能性があることを表現するために使用されるクラスはどれですか？', '{"A": "Nullable", "B": "Optional", "C": "Maybe", "D": "Void"}', 'B', 'SINGLE_CHOICE'),
('42d27cf1-5956-493e-ae94-a222f51d3681', '660e8400-e29b-41d4-a716-446655440022', 'Streamの処理結果をListやSetにまとめるために使用する終端操作はどれですか？', '{"A": "gather", "B": "group", "C": "collect", "D": "assemble"}', 'C', 'SINGLE_CHOICE'),
('b5fc7b58-98d1-4b74-9246-263bf80e4ac2', '660e8400-e29b-41d4-a716-446655440022', 'テキストファイルを効率的に読み込むためにバッファリングを行うクラスはどれですか？', '{"A": "FileReader", "B": "BufferedReader", "C": "FileInputStream", "D": "Scanner"}', 'B', 'SINGLE_CHOICE'),
('9ff4fc3c-88d6-4299-91ea-f11cf4a78712', '660e8400-e29b-41d4-a716-446655440022', 'オブジェクトの状態をバイト列に変換して保存・転送可能にすることを何と呼びますか？', '{"A": "コンパイル", "B": "シリアライズ（直列化）", "C": "暗号化", "D": "パース"}', 'B', 'SINGLE_CHOICE'),
('10488540-827d-4a11-9dcb-04065f2badfc', '660e8400-e29b-41d4-a716-446655440022', 'ファイルやディレクトリのパス名を抽象的に表現するクラスはどれですか？', '{"A": "File", "B": "PathName", "C": "Directory", "D": "Folder"}', 'A', 'SINGLE_CHOICE'),
('01852bd0-6f01-4d94-8835-2fae96caa3c4', '660e8400-e29b-41d4-a716-446655440022', 'Consumerインターフェースが持つ抽象メソッドはどれですか？', '{"A": "get", "B": "apply", "C": "test", "D": "accept"}', 'D', 'SINGLE_CHOICE')
ON CONFLICT (id) DO NOTHING;