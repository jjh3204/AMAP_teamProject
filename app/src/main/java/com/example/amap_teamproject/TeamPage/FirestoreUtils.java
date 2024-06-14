package com.example.amap_teamproject.TeamPage;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirestoreUtils {

    private FirebaseFirestore db;

    public FirestoreUtils() {
        this.db = FirebaseFirestore.getInstance();
    }

    // 게시글 삭제 함수
    public Task<Void> deletePostWithCommentsAndReplies(String type, String documentId, String postId) {
        CollectionReference postRef = db.collection(type).document(documentId).collection("posts");

        return deleteCollectionRecursive(postRef.document(postId).collection("comments"))
                .continueWithTask(task -> postRef.document(postId).delete());
    }

    // 댓글 삭제 함수
    public Task<Void> deleteCommentWithReplies(String type, String documentId, String postId, String commentId) {
        CollectionReference commentRef = db.collection(type).document(documentId).collection("posts")
                .document(postId).collection("comments");

        return deleteCollectionRecursive(commentRef.document(commentId).collection("replies"))
                .continueWithTask(task -> commentRef.document(commentId).delete());
    }

    // 재귀적으로 하위 컬렉션을 삭제하는 함수
    private Task<Void> deleteCollectionRecursive(CollectionReference collection) {
        return collection.get().continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }

            QuerySnapshot snapshots = task.getResult();
            List<Task<Void>> tasks = new ArrayList<>();
            for (QueryDocumentSnapshot snapshot : snapshots) {
                tasks.add(deleteCollectionRecursive(snapshot.getReference().collection("replies")));
                tasks.add(snapshot.getReference().delete());
            }

            return Tasks.whenAll(tasks);
        });
    }
}