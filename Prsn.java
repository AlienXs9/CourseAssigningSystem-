package prjct;



    public abstract class Prsn {
        protected String id;
        protected String name;
        protected String email;

        public Prsn(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public abstract void viewProfile();
    }

