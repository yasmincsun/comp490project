/**
 * Date: February 16, 2026
 * @author Miguel A.
 * @version 1.00
 */

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:5173") // React dev server
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    
    //Handles Post requests in /api/posts
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody @Valid CreatePostRequest request
    ) {
        Post post = new Post(); // Create a new Post entity
        // Sets the fields of the Post entity based on the incoming request data: Title, Content, and Category.
        post.setTitle(request.getTitle());  
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        Post savedPost = postRepository.save(post); //Saves the new Post to the database using PostRepository.
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    //Handles GET requests to retrieve all posts from the database.
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}