package quynh.vtit.task00017.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import quynh.vtit.task00017.base.enums.CategoryType;
import quynh.vtit.task00017.domain.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
    Select c from Category c 
        where c.active = true and (c.user.id = :userId or c.system = true)
        order by c.createdAt desc
    """)
    List<Category> findCategoriesAvailable(@Param("userId") Long userId);


    Optional<Category> findByIdAndUserIdAndActiveTrue(Long id, Long userId);

    @Query("""
    Select c from Category c
        where c.id = :id
            and c.active = true
            and (c.user.id = :userId or c.system = true)
    """)
    Optional<Category> findAvailableById(@Param("id") Long id, @Param("userId") Long userId);

    boolean existsByUserIdAndNameIgnoreCaseAndCategoryTypeAndActiveTrue(
            Long userId,
            String name,
            CategoryType categoryType
    );

    boolean existsByUserIdAndNameIgnoreCaseAndCategoryTypeAndActiveTrueAndIdNot(
            Long userId,
            String name,
            CategoryType categoryType,
            Long id
    );
}
