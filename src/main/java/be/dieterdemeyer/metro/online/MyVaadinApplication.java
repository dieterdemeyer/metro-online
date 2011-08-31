package be.dieterdemeyer.metro.online;

import com.vaadin.Application;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Window;

import java.io.File;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends Application implements Tree.ExpandListener {

    // Filesystem explorer panel and it's components
    private final Panel explorerPanel = new Panel("Filesystem explorer");

    private final Tree tree = new Tree();

    private Window window;

    /*@Override
    public void init() {
        window = new Window("Metro Online");
        setMainWindow(window);
        window.addComponent(new Button("Click Me"));
    }*/

    @Override
    public void init() {
        final Window main = new Window("Tree filesystem demo");
        setMainWindow(main);

        // Main window contains heading and panel
        main.addComponent(new Label("<h2>Tree demo</h2>", Label.CONTENT_XHTML));

        // configure file structure panel
        main.addComponent(explorerPanel);
        explorerPanel.addComponent(tree);
        explorerPanel.setHeight("400");

        // "this" handles tree's expand event
        tree.addListener(this);

        // Get directory
        final File sampleDir = new File(System.getProperty("user.home") + "/Downloads");
        // populate tree's root node with example directory
        if (sampleDir != null) {
            populateNode(sampleDir.getAbsolutePath(), null);
        }

        tree.addListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent itemClickEvent) {
                System.out.println("Item clicked: " + itemClickEvent.getItemId());
            }
        });
    }

    /**
     * Handle tree expand event, populate expanded node's childs with new files
     * and directories.
     */
    public void nodeExpand(Tree.ExpandEvent event) {
        final Item i = tree.getItem(event.getItemId());
        if (!tree.hasChildren(i)) {
            // populate tree's node which was expanded
            populateNode(event.getItemId().toString(), event.getItemId());
        }
    }

    /**
     * Populates files to tree as items. In this example items are of String
     * type that consist of file path. New items are added to tree and item's
     * parent and children properties are updated.
     *
     * @param file   path which contents are added to tree
     * @param parent for added nodes, if null then new nodes are added to root node
     */
    private void populateNode(String file, Object parent) {
        final File subdir = new File(file);
        final File[] files = subdir.listFiles();
        for (int x = 0; x < files.length; x++) {
            try {
                // add new item (String) to tree
                final String path = files[x].getCanonicalPath();
                tree.addItem(path);
                // set parent if this item has one
                if (parent != null) {
                    tree.setParent(path, parent);
                }
                // check if item is a directory and read access exists
                if (files[x].isDirectory() && files[x].canRead()) {
                    // yes, childrens therefore exists
                    tree.setChildrenAllowed(path, true);
                } else {
                    // no, childrens therefore do not exists
                    tree.setChildrenAllowed(path, false);
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
