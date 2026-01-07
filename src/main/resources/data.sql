-- 初期ユーザーデータ
-- パスワード: admin123 ($2a$10$ZVp3xzVThoTkrKsPUEe.V.A7HPduCd9Y9laAkxNpnHKJ91dPJ/daK)
-- パスワード: user123 ($2a$10$cP7gGuoXml/EyL6mdEaBou92Hz2ScD32YVXdH/A41EZPfKqLqH9Mi)
INSERT INTO users (id, username, password, display_name, role, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'admin', '$2a$10$ZVp3xzVThoTkrKsPUEe.V.A7HPduCd9Y9laAkxNpnHKJ91dPJ/daK', '管理者', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('550e8400-e29b-41d4-a716-446655440002', 'testuser', '$2a$10$cP7gGuoXml/EyL6mdEaBou92Hz2ScD32YVXdH/A41EZPfKqLqH9Mi', 'テストユーザー', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET
    password = EXCLUDED.password,
    display_name = EXCLUDED.display_name,
    role = EXCLUDED.role;

-- 章データ
INSERT INTO chapters (id, chapter_code, title, sort_order) VALUES
('660e8400-e29b-41d4-a716-446655440013', 'chap3', '3章:値と計算', 3),
('660e8400-e29b-41d4-a716-446655440014', 'chap4', '4章:変数と型', 4),
('660e8400-e29b-41d4-a716-446655440015', 'chap5', '5章:標準API', 5),
('660e8400-e29b-41d4-a716-446655440016', 'chap7', '7章:条件分岐', 7),
('660e8400-e29b-41d4-a716-446655440017', 'etc', '章以外(Linuxコマンド/システム開発プロセステスト)', 8),
('660e8400-e29b-41d4-a716-446655440018', 'chap8', '8章:データ構造', 9),
('660e8400-e29b-41d4-a716-446655440019', 'chap9', '9章:繰り返し', 10),
('660e8400-e29b-41d4-a716-446655440020', 'chap10', '10章:データ構造の処理', 11),
('660e8400-e29b-41d4-a716-446655440021', 'chap11', '11章:メソッド', 12)
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

-- Chapter 7: Conditional Branching
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000000701', '660e8400-e29b-41d4-a716-446655440016', 'if文の条件式に指定できる型は？', '{"A": "int", "B": "boolean", "C": "String", "D": "double"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000702', '660e8400-e29b-41d4-a716-446655440016', '次のうち短絡評価（ショートサーキット）を行う演算子は？', '{"A": "&", "B": "|", "C": "&&", "D": "^"}', 'C', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000703', '660e8400-e29b-41d4-a716-446655440016', 'switch文で使用できない型は？（一般的な範囲）', '{"A": "int", "B": "String", "C": "enum", "D": "double"}', 'D', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000704', '660e8400-e29b-41d4-a716-446655440016', 'switch文でcaseを抜けずに次のcaseへ処理が進むことを何と呼ぶ？', '{"A": "fall-through", "B": "break-out", "C": "short-circuit", "D": "override"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000705', '660e8400-e29b-41d4-a716-446655440016', '三項演算子の形式として正しいものは？', '{"A": "cond ? a : b", "B": "cond : a ? b", "C": "cond ?? a : b", "D": "cond -> a : b"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000706', '660e8400-e29b-41d4-a716-446655440016', '次の比較で、数値として等しいことを判定する演算子は？', '{"A": "=", "B": "==", "C": "!=", "D": "equals"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000707', '660e8400-e29b-41d4-a716-446655440016', 'if (a > 0) { ... } else { ... } の else ブロックが実行される条件は？', '{"A": "a > 0", "B": "a == 0", "C": "a <= 0", "D": "a < 0"}', 'C', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000708', '660e8400-e29b-41d4-a716-446655440016', '次のうち論理否定演算子は？', '{"A": "!", "B": "~", "C": "^", "D": "?"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000709', '660e8400-e29b-41d4-a716-446655440016', 'if文のネストを浅くするために使われることが多い制御文は？', '{"A": "continue", "B": "break", "C": "return", "D": "goto"}', 'C', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000710', '660e8400-e29b-41d4-a716-446655440016', 'switch文でどのcaseにも一致しない場合に実行されるラベルは？', '{"A": "default", "B": "else", "C": "otherwise", "D": "none"}', 'A', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- etc: Linux Commands / Process Basics
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000000801', '660e8400-e29b-41d4-a716-446655440017', '現在の作業ディレクトリを表示するLinuxコマンドは？', '{"A": "pwd", "B": "cd", "C": "ls", "D": "who"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000802', '660e8400-e29b-41d4-a716-446655440017', 'ファイル一覧を表示するLinuxコマンドは？', '{"A": "cat", "B": "ls", "C": "rm", "D": "mv"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000803', '660e8400-e29b-41d4-a716-446655440017', 'ディレクトリを作成するLinuxコマンドは？', '{"A": "mkdir", "B": "rmdir -p", "C": "touch", "D": "cp"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000804', '660e8400-e29b-41d4-a716-446655440017', 'ファイルの中身を表示するコマンドとして最も基本的なのは？', '{"A": "cat", "B": "grep", "C": "chmod", "D": "kill"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000805', '660e8400-e29b-41d4-a716-446655440017', '文字列を検索するLinuxコマンドは？', '{"A": "find", "B": "grep", "C": "ps", "D": "top"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000806', '660e8400-e29b-41d4-a716-446655440017', '実行権限を変更するコマンドは？', '{"A": "chmod", "B": "chown", "C": "umask", "D": "sudo"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000807', '660e8400-e29b-41d4-a716-446655440017', 'プロセス一覧を表示するコマンドは？', '{"A": "ps", "B": "ssh", "C": "tar", "D": "ping"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000808', '660e8400-e29b-41d4-a716-446655440017', 'ファイルをコピーするコマンドは？', '{"A": "cp", "B": "mv", "C": "rm", "D": "ln"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000809', '660e8400-e29b-41d4-a716-446655440017', 'システム開発プロセスで「要件定義」の次に来ることが多い工程は？（一般的なウォーターフォール）', '{"A": "テスト", "B": "設計", "C": "運用", "D": "保守"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000810', '660e8400-e29b-41d4-a716-446655440017', 'バージョン管理システムの例として適切なのは？', '{"A": "Git", "B": "HTTP", "C": "SQL", "D": "DNS"}', 'A', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 8: Data Structures
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000000901', '660e8400-e29b-41d4-a716-446655440018', '配列の要素数を取得するプロパティは？', '{"A": "length", "B": "size()", "C": "count()", "D": "len"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000902', '660e8400-e29b-41d4-a716-446655440018', 'ArrayListの要素数を取得するメソッドは？', '{"A": "length", "B": "size()", "C": "capacity()", "D": "count()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000903', '660e8400-e29b-41d4-a716-446655440018', 'Listに要素を末尾追加するメソッドは？', '{"A": "put()", "B": "add()", "C": "append()", "D": "push()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000904', '660e8400-e29b-41d4-a716-446655440018', 'Mapにキーと値を格納するメソッドは？', '{"A": "add()", "B": "put()", "C": "set()", "D": "insert()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000905', '660e8400-e29b-41d4-a716-446655440018', '配列のインデックスは通常いくつから始まる？', '{"A": "0", "B": "1", "C": "-1", "D": "環境依存"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000906', '660e8400-e29b-41d4-a716-446655440018', 'HashMapがキーの順序を保持するか？', '{"A": "常に保持する", "B": "保持しない", "C": "昇順で保持する", "D": "降順で保持する"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000907', '660e8400-e29b-41d4-a716-446655440018', 'Listの先頭要素を取り出す（取得する）メソッドとして正しいものは？', '{"A": "get(0)", "B": "first()", "C": "head()", "D": "peekFirst()"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000908', '660e8400-e29b-41d4-a716-446655440018', 'Mapから値を取得するメソッドは？', '{"A": "fetch(key)", "B": "get(key)", "C": "read(key)", "D": "value(key)"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000909', '660e8400-e29b-41d4-a716-446655440018', '配列アクセスで範囲外を参照した場合に発生しやすい例外は？', '{"A": "NullPointerException", "B": "ArrayIndexOutOfBoundsException", "C": "IOException", "D": "NumberFormatException"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000000910', '660e8400-e29b-41d4-a716-446655440018', 'Listは重複要素を持てるか？', '{"A": "持てない", "B": "持てる", "C": "型による", "D": "実装による"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 9: Loops
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000001001', '660e8400-e29b-41d4-a716-446655440019', 'for文の基本構文として正しいものは？', '{"A": "for (init; cond; update)", "B": "for (cond; init; update)", "C": "for (init; update; cond)", "D": "for (cond)"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001002', '660e8400-e29b-41d4-a716-446655440019', 'while文は条件がfalseの場合でも最低1回実行されるか？', '{"A": "される", "B": "されない", "C": "Javaのバージョンによる", "D": "JVMによる"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001003', '660e8400-e29b-41d4-a716-446655440019', 'do-while文の特徴として正しいものは？', '{"A": "条件がfalseでも最低1回実行される", "B": "必ず無限ループになる", "C": "breakが使えない", "D": "continueが使えない"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001004', '660e8400-e29b-41d4-a716-446655440019', 'ループを強制終了するキーワードは？', '{"A": "stop", "B": "break", "C": "exit", "D": "end"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001005', '660e8400-e29b-41d4-a716-446655440019', '次のループで次の繰り返しへスキップするキーワードは？', '{"A": "skip", "B": "continue", "C": "next", "D": "pass"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001006', '660e8400-e29b-41d4-a716-446655440019', '拡張for文（for-each）の構文として正しいものは？', '{"A": "for (T x : collection)", "B": "for (collection : T x)", "C": "foreach (T x in collection)", "D": "for (T x <- collection)"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001007', '660e8400-e29b-41d4-a716-446655440019', '無限ループになりやすい記述は？', '{"A": "for(;;)", "B": "for(i=0;i<10;i++)", "C": "while(i<10)", "D": "do{...}while(false)"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001008', '660e8400-e29b-41d4-a716-446655440019', 'ループ変数iが0から4までの合計回数として正しいのは？（i=0; i<5; i++）', '{"A": "4回", "B": "5回", "C": "6回", "D": "0回"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001009', '660e8400-e29b-41d4-a716-446655440019', 'breakはどこで使える？', '{"A": "ifのみ", "B": "ループやswitch", "C": "クラス定義のみ", "D": "importのみ"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001010', '660e8400-e29b-41d4-a716-446655440019', 'continueの効果として正しいものは？', '{"A": "ループを終了する", "B": "現在の反復を終了して次へ進む", "C": "メソッドを終了する", "D": "例外を投げる"}', 'B', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 10: Processing Data Structures
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000001101', '660e8400-e29b-41d4-a716-446655440020', 'Listを昇順ソートする代表的なメソッドは？', '{"A": "Collections.sort(list)", "B": "Arrays.sort(list)", "C": "list.order()", "D": "list.sortAsc()"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001102', '660e8400-e29b-41d4-a716-446655440020', 'Mapのキーと値の組を走査するのに便利なメソッドは？', '{"A": "values()", "B": "entrySet()", "C": "keyList()", "D": "pairs()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001103', '660e8400-e29b-41d4-a716-446655440020', '配列を拡張forで走査する書き方はどれ？', '{"A": "for (int x : arr)", "B": "for (arr : int x)", "C": "foreach (x in arr)", "D": "for (x <- arr)"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001104', '660e8400-e29b-41d4-a716-446655440020', 'Listから特定要素を削除するメソッドは？', '{"A": "delete()", "B": "remove()", "C": "drop()", "D": "erase()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001105', '660e8400-e29b-41d4-a716-446655440020', 'Iteratorを使う主な利点として適切なのは？', '{"A": "高速化が必ずできる", "B": "走査しながら安全に削除できる", "C": "型が不要になる", "D": "例外が出なくなる"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001106', '660e8400-e29b-41d4-a716-446655440020', 'Listの各要素に同じ処理を適用する典型的な方法は？', '{"A": "ループで回す", "B": "if文で回す", "C": "switch文で回す", "D": "try-catchで回す"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001107', '660e8400-e29b-41d4-a716-446655440020', '配列をコピーする代表的なメソッドは？', '{"A": "System.arraycopy", "B": "System.copy", "C": "Array.copy", "D": "Copy.array"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001108', '660e8400-e29b-41d4-a716-446655440020', 'Listを配列に変換するメソッドは？', '{"A": "toArray()", "B": "asArray()", "C": "array()", "D": "convert()"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001109', '660e8400-e29b-41d4-a716-446655440020', 'Mapにキーが存在するか確認するメソッドは？', '{"A": "hasKey()", "B": "containsKey()", "C": "existsKey()", "D": "inKey()"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001110', '660e8400-e29b-41d4-a716-446655440020', 'Listの指定位置に要素を挿入するメソッド（例: index=0）は？', '{"A": "add(0, x)", "B": "insert(0, x)", "C": "put(0, x)", "D": "set(0, x)"}', 'A', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

-- Chapter 11: Methods
INSERT INTO questions (id, chapter_id, question_text, options, correct_answer, question_type) VALUES
('a1b2c3d4-0000-4000-8000-000000001201', '660e8400-e29b-41d4-a716-446655440021', '戻り値がないメソッドの戻り値型は？', '{"A": "null", "B": "void", "C": "Object", "D": "None"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001202', '660e8400-e29b-41d4-a716-446655440021', '同じメソッド名で引数が異なるメソッドを定義することを何という？', '{"A": "オーバーライド", "B": "オーバーロード", "C": "カプセル化", "D": "抽象化"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001203', '660e8400-e29b-41d4-a716-446655440021', 'staticメソッドは何から直接呼び出せる？', '{"A": "クラス名", "B": "インスタンス変数", "C": "this", "D": "super"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001204', '660e8400-e29b-41d4-a716-446655440021', 'メソッドの引数に値を渡すことを一般に何と呼ぶ？', '{"A": "パラメータ", "B": "引数", "C": "戻り値", "D": "例外"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001205', '660e8400-e29b-41d4-a716-446655440021', 'return文の役割として正しいものは？', '{"A": "ループを終了する", "B": "メソッドを終了し（必要なら）値を返す", "C": "例外を無視する", "D": "クラスを終了する"}', 'B', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001206', '660e8400-e29b-41d4-a716-446655440021', 'メソッドのシグネチャに通常含まれないものは？', '{"A": "メソッド名", "B": "引数の型", "C": "戻り値型", "D": "引数の個数"}', 'C', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001207', '660e8400-e29b-41d4-a716-446655440021', '同じクラス内の別メソッドを呼び出す基本的な書き方は？', '{"A": "methodName()", "B": "call methodName", "C": "invoke(methodName)", "D": "run methodName"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001208', '660e8400-e29b-41d4-a716-446655440021', '引数の値をメソッド内で変更しても呼び出し元の変数が必ず変わる、は正しい？', '{"A": "常に正しい", "B": "常に誤り", "C": "プリミティブは変わらず参照は変わることがある", "D": "Stringだけ変わる"}', 'C', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001209', '660e8400-e29b-41d4-a716-446655440021', 'メソッドが例外を呼び出し元に通知するために使うキーワードは？', '{"A": "throws", "B": "throwing", "C": "catch", "D": "finally"}', 'A', 'SINGLE_CHOICE'),
('a1b2c3d4-0000-4000-8000-000000001210', '660e8400-e29b-41d4-a716-446655440021', 'mainメソッドのシグネチャとして一般的に正しいものは？', '{"A": "public static void main(String[] args)", "B": "static public int main(String args)", "C": "public void main(String[] args)", "D": "public static main(String[] args)"}', 'A', 'SINGLE_CHOICE')
ON CONFLICT (id) DO UPDATE SET 
    question_text = EXCLUDED.question_text,
    options = EXCLUDED.options,
    correct_answer = EXCLUDED.correct_answer,
    question_type = EXCLUDED.question_type;

