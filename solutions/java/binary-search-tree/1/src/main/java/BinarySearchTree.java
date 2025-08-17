import java.util.ArrayList;
import java.util.List;

class BinarySearchTree<T extends Comparable<T>> {

    private Node<T> root;
    private final List<T> values = new ArrayList<>();

    void insert(T value) {
        values.add(value);
        if (root == null) {
            root = new BinarySearchTree.Node<>(value);
        } else {
            insertHelper(root, value);
        }
    }

    private void insertHelper(Node<T> node, T value) {
        if (value.compareTo(node.data) <= 0) {
            if (node.left == null) {
                node.left = new BinarySearchTree.Node<>(value);
            } else {
                insertHelper(node.left, value);
            }
        } else {
            if (node.right == null) {
                node.right = new BinarySearchTree.Node<>(value);
            } else {
                insertHelper(node.right, value);
            }
        }
    }

    List<T> getAsSortedList() {
        return sortedHelper(root);
    }

    private List<T> sortedHelper(Node<T> node) {
        if (node == null) {return List.of();}

        List<T> result = new ArrayList<>(sortedHelper(node.left));
        result.add(node.data);
        result.addAll(sortedHelper(node.right));

        return result;
    }

    List<T> getAsLevelOrderList() {
        return values;
    }

    Node<T> getRoot() {
        return root;
    }

    static class Node<T> {

        private final T data;
        private Node<T> left;
        private Node<T> right;

        Node(T data) {
            this.data = data;
        }

        Node<T> getLeft() {
            return left;
        }

        Node<T> getRight() {
            return right;
        }

        T getData() {
            return data;
        }

    }
}
