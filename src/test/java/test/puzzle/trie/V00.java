package test.puzzle.trie;

import java.util.HashMap;
import java.util.Map;

class V00 {

    /******************************
     * トライ (データ構造) 出典: フリー百科事典『ウィキペディア（Wikipedia）』
     * 
     * トライ（英: trie）、デジタル木（英: digital tree）、プレフィックス木（英: prefix tree）とは、
     * 順序付き木の一種。あるノードの配下の全ノードは、自身に対応する文字列に共通するプレフィックス（接頭部）があり、
     * ルート（根）には空の文字列が対応している。値は一般に全ノードに対応して存在するわけではなく、
     * 末端ノードや一部の中間ノードだけがキーに対応した値を格納している。2分探索木と異なり、
     * 各ノードに個々のキーが格納されるのではなく、木構造上のノードの位置とキーが対応している。
     */

    /***
     * トライは、検索と挿入の操作をサポートするノードのツリーである。Find はキー文字列の値を返し，Insert
     * は文字列（キー）と値をトライに挿入する。Insert と Find はどちらも O(m) 時間で実行され、m はキーの長さである。
     * 
     * シンプルな Node クラスを使用して、トライ内のノードを表現することができる。
     * 
     * <pre>
     * <code>
     *  class Node:
     *     def __init__(self) -> None:
     *         # （この実装のように）子にディクショナリーを使用しても、デフォルトでは子を辞書式ソートしないことに注意。
     *         # これは、次のセクション（ソート）で説明する辞書式ソートで必要である。
     *         self.children: Dict[str, Node] = {}  # 文字からノードへのマッピング
     *         self.value: Optional[Any] = None
     * </code>
     * </pre>
     */
    static class Node {
        Map<Character, Node> children = new HashMap<>();
        Object value = null;

        /***
         * 
         * children はノードの子への文字のディクショナリーであり、
         * 「終端」ノードは完全な文字列を表すノードであると言われていることに注意。
         * 
         * トライの値は以下のように調べることができる。
         * 
         * <pre>
         * <code>
         *  def find(node: Node, key: str) -> Optional[Any]:
         *      """ノードのキーで値を検索する"""
         *      for char in key:
         *          if char in node.children:
         *              node = node.children[char]
         *          else:
         *              return None
         *      return node.value
         * </code>
         * </pre>
         */
        public Object find(String key) {
            Node node = this;
            for (int i = 0, length = key.length(); i < length; ++i) {
                node = node.children.get(key.charAt(i));
                if (node == null)
                    return null;
            }
            return node.value;
        }

        /***
         * このルーチンを少し変更して以下のために利用することができる。
         * 
         * トライに指定された接頭辞で始まる単語があるかどうかを確認するため、そして
         * 指定された文字列のいくつかの接頭辞に対応する最も深いノードを返すため。
         * 挿入は、挿入する文字列に応じてトライを歩き、トライに含まれていない文字列の接尾辞に
         * 対応する新しいノードを追加することで行われる。
         * 
         * <pre>
         * <code>
         *  def insert(node: Node, key: str, value: Any) -> None:
         *      """キーと値のペアをノードに挿入する"""
         *      for char in key:
         *          if char not in node.children:
         *              node.children[char] = Node()
         *          node = node.children[char]
         *      node.value = value
         * </code>
         * </pre>
         */
        public void insert(String key, Object value) {
            Node node = this;
            for (int i = 0, length = key.length(); i < length; ++i)
                node = children.computeIfAbsent(key.charAt(i), k -> new Node());
            node.value = value;
        }
    }

}
